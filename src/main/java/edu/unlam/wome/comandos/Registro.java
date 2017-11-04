package edu.unlam.wome.comandos;

import java.io.IOException;

import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.Paquete;
import edu.unlam.wome.mensajeria.PaqueteUsuario;

/**
 * Clase Registro.
 * Extiende de la clase ComandosServer
 */
public class Registro extends ComandosServer {

	/* (non-Javadoc)
	 * @see mensajeria.Comando#ejecutar()
	 */
	@Override
	public void ejecutar() {
		Paquete paqueteSv = new Paquete(null, 0);
		paqueteSv.setComando(Comando.REGISTRO);

        escuchaCliente.setPaqueteUsuario((PaqueteUsuario) (getGson().
        		fromJson(getCadenaLeida(), PaqueteUsuario.class)).clone());

		// Si el usuario se pudo registrar, le envío un mensaje de éxito
		try {
			if (Servidor.getConector().registrarUsuario(escuchaCliente.getPaqueteUsuario())) {
				paqueteSv.setMensaje(Paquete.getMsjExito());
				escuchaCliente.getSalida().writeObject(getGson().toJson(paqueteSv));

				// Si el usuario no se pudo registrar, le envío un mensaje de fracaso
			} else {
				paqueteSv.setMensaje(Paquete.getMsjFracaso());
				escuchaCliente.getSalida().writeObject(getGson().toJson(paqueteSv));
			}
		} catch (IOException e) {
			Servidor.getLog().append("Falló al intentar enviar registro\n");
		}

	}

}
