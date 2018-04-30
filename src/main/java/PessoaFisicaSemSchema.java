import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PessoaFisicaSemSchema extends PessoaSemSchema {
	private String cpf;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	

}
