package br.com.brejaonline.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.brejaonline.model.Cerveja;
import br.com.brejaonline.model.Estoque;
import br.com.brejaonline.model.exception.RecursoSemIdentificadorException;
import br.com.brejaonline.model.rest.Cervejas;


@WebServlet(value="/cervejas/*")
public class CervejaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Estoque estoque = new Estoque();
	private static JAXBContext context;
	
	static {
		try {
			context = JAXBContext.newInstance(Cervejas.class);
		} catch(JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void escreveXML(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object objetoAEscrever = localizaObjetoASerEnviado(request);
		if ( objetoAEscrever == null ) {
			response.sendError(404);
			return;
		}
		
		try {
			response.setContentType("application/xml;charset=UTF-8");
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(objetoAEscrever, response.getWriter());
		} catch (JAXBException e) {
			
		}

	}
	
	private void escreveJSON(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object objetoAEscrever = localizaObjetoASerEnviado(request);
		
		if ( objetoAEscrever == null ) {
			response.sendError(404);
			return;
		}
		
		response.setContentType("application/json;charset=UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getWriter(), objetoAEscrever);
	}
	
	private String obtemIdentificador(HttpServletRequest request) throws RecursoSemIdentificadorException {
		String requestUri = request.getRequestURI();
		String[] pedacosDaUri = requestUri.split("/");
		
		boolean contextoCervejasEncontrado = false;
		for (String contexto : pedacosDaUri) {
			if( contexto.equals("cervejas") ) {
				contextoCervejasEncontrado = true;
				continue;
			}
			if ( contextoCervejasEncontrado ) {
				try {
					return URLDecoder.decode(contexto, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					return contexto;
				}
			}
		}		
		throw new RecursoSemIdentificadorException("Recurso sem identificador");
	}
	
	private Object localizaObjetoASerEnviado(HttpServletRequest request) throws UnsupportedEncodingException {
		Object objeto = null;
		try {
			String identificador = obtemIdentificador(request);
			objeto = estoque.recuperarCervejaPeloNome(identificador);
		} catch (RecursoSemIdentificadorException e) {
			Cervejas cervejas = new Cervejas();
			cervejas.setCervejas(new ArrayList<>(estoque.listarCervejas()));
			objeto = cervejas;
		}
		return objeto;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String acceptHeader = request.getHeader("Accept");
		
		if (acceptHeader == null || acceptHeader.contains("application/xml") ) {
			escreveXML(request, response);
		}
		else if ( acceptHeader.contains("application/json") ) {
			escreveJSON(request, response);
		} else {
			response.sendError(415);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String identificador = null;
			String tipoDeConteudo = request.getContentType();
			try {
				identificador = obtemIdentificador(request);
			} catch (RecursoSemIdentificadorException e) {
				response.sendError(400, e.getMessage());
			}
			
			if (identificador != null && estoque.recuperarCervejaPeloNome(identificador) != null) {
				response.sendError(409, "Já existe uma cerveja com esse nome");
				return;
			}
			
			List<String> linhas = IOUtils.readLines(request.getInputStream());
			StringBuilder stringBuilder = new StringBuilder();
			for(String linha : linhas) {
				stringBuilder.append(linha);
			}
			
			Cerveja cerveja;
			
			if ( tipoDeConteudo.equals("application/json") ) {
				ObjectMapper mapper = new ObjectMapper();
				cerveja = mapper.readValue(stringBuilder.toString(), Cerveja.class);
				cerveja.setNome(identificador);
				estoque.adicionarCerveja(cerveja);
				String requestURI = request.getRequestURI();
				response.setHeader("Location", requestURI);
				response.setStatus(201);
				escreveJSON(request, response);
				return;
			} else if (tipoDeConteudo.equals("application/xml") || tipoDeConteudo.equals("text/xml") ){
				Unmarshaller unmarshaller = context.createUnmarshaller();
				cerveja = (Cerveja) unmarshaller.unmarshal(request.getInputStream());
				cerveja.setNome(identificador);
				estoque.adicionarCerveja(cerveja);
				String requestURI = request.getRequestURI();
				response.setHeader("Location", requestURI);
				response.setStatus(201);
				escreveXML(request, response);
				return;
			} else {
				response.sendError(415, "Tipo de dado não suportado");
				return;
			}				
		} catch(JAXBException | RecursoSemIdentificadorException e) {
			
		}
	}
}
