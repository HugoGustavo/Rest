import javax.xml.bind.JAXB;

import br.com.brejaonline.endereco.v1.Endereco;

public class Principal {

	public static void main(String[] args) {
		PessoaFisicaSemSchema pessoaFisicaSemSchema = new PessoaFisicaSemSchema();
		pessoaFisicaSemSchema.setId((long) 0);
		pessoaFisicaSemSchema.setCpf("12345678909");
		pessoaFisicaSemSchema.setNome("Alexandre Saudate");
		
		Endereco endereco = new Endereco();
		endereco.setCep("12345-678");
		endereco.setLogradouro("logradouro");
		
		pessoaFisicaSemSchema.getEndereco().add(endereco);
		
		JAXB.marshal(pessoaFisicaSemSchema, System.out);

	}

}
