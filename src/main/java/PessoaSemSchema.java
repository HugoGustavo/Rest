import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import br.com.brejaonline.endereco.v1.Endereco;
import br.com.brejaonline.pessoa.v1.PessoaFisica;

@XmlSeeAlso ({
	PessoaFisica.class
})
public abstract class PessoaSemSchema {
	private String nome;
	private List<Endereco> endereco = new ArrayList<Endereco>();
	private Long id;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public List<Endereco> getEndereco() {
		return endereco;
	}
	
	public void setEndereco(List<Endereco> endereco) {
		this.endereco = endereco;
	}
	
	@XmlAttribute(name="id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

}
