package comandos;

import java.io.IOException;

import mensajeria.Paquete;
import servidor.Servidor;

/**
 * Clase Salir.
 * Extiende de la clase ComandosServer
 */
public class Salir extends ComandosServer {

	/* (non-Javadoc)
	 * @see mensajeria.Comando#ejecutar()
	 */
	@Override
	public void ejecutar() {
		// Cierro todo
		try {
			escuchaCliente.getEntrada().close();
			escuchaCliente.getSalida().close();
			escuchaCliente.getSocket().close();
		} catch (IOException e) {
			Servidor.getLog().append("Falló al intentar salir \n");

		}

		// Lo elimino de los clientes conectados
		Servidor.getClientesConectados().remove(this);
		Paquete paquete = (Paquete) getGson().fromJson(getCadenaLeida(), Paquete.class);
		// Indico que se desconecto
		Servidor.getLog().append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
	}

}
