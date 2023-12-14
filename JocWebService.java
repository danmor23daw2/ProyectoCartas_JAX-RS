package proyecto.cartas.proyectocartas;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;

/**
 * WebService que representa un juego de cartas de UNO
 * @author sergi.grau@fje.edu
 * @version 1.0
 * @date  9.11.23
 */
@Path("/")
public class JocWebService {
    private static ArrayList<String> partidas = new ArrayList<>();

    /**
     * Inicia un juego de UNO con un código de partida
     */
    @POST
    @Path("/iniciarJoc/{codiPartida}")
    @Produces(MediaType.TEXT_PLAIN)
    public byte iniciarJoc(@PathParam("codiPartida") int codiPartida) {
        if (codiPartida > partidas.size()) {
            ArrayList<String> mazo = new ArrayList<>();

            partidas.add(String.valueOf(mazo));
        }
        return (byte) partidas.size();
    }

    /**
     * Obtiene la lista de cartas de un jugador en formato JSON
     *
     * @param codiPartida Código de la partida
     * @return Lista de cartas en formato JSON
     */
    @GET
    @Path("/mostrarCartes/{codiPartida}")
    @Produces(MediaType.APPLICATION_JSON)
    public String mostrarCartes(@PathParam("codiPartida") int codiPartida) {
        if (codiPartida <= partidas.size() && codiPartida > 0) {
            return partidas.get(codiPartida - 1);
        } else {
            return String.valueOf(new ArrayList<>()); // Devuelve una lista vacía si el código de partida no es válido
        }
    }

    /**
     * Tira una carta en el juego
     * @param codiPartida Código de la partida
     * @param carta Carta a ser tirada
     * @return Respuesta del juego después de tirar la carta
     */
    @PUT
    @Path("/tirarCarta/{codiPartida}/{carta}")
    @Produces(MediaType.TEXT_PLAIN)
    public String tirarCarta(@PathParam("codiPartida") int codiPartida, @PathParam("carta") String carta) {
        if (codiPartida <= partidas.size() && codiPartida > 0) {
            String mazo = partidas.get(codiPartida - 1);

            if (mazo.contains(carta)) {

                return "Has tirado una carta";
            } else {
                return "La carta no está en el mazo de la partida.";
            }
        } else {
            return "Código de partida no válido.";
        }
    }
    /**
     * Mueve un jugador en el juego pasando su turno
     * @param codiPartida Código de la partida
     * @return Respuesta del juego después de pasar el turno
     */
    @PUT
    @Path("/moureJugador/{codiPartida}/passa")
    @Produces(MediaType.TEXT_PLAIN)
    public String moureJugadorPassa(@PathParam("codiPartida") int codiPartida) {

        return "El jugador a passado";
    }
    /**
     * Mueve un jugador en el juego haciendo una apuesta
     * @param codiPartida Código de la partida
     * @param robar roba cartas
     * @param quantitat Cantidad apostada
     * @return Respuesta del juego después de realizar el movimiento
     */
    @PUT
    @Path("/moureJugador/{codiPartida}/{robar}/{quantitat}")
    @Produces(MediaType.TEXT_PLAIN)
    public String moureJugador(@PathParam("codiPartida") int codiPartida,
                               @PathParam("robar") String robar,
                               @PathParam("quantitat") int quantitat) {

        return "El jugador a robado cartas";
    }
    /**
     * Finaliza un juego de UNO
     * @param codiPartida Código de la partida
     */
    @DELETE
    @Path("/acabarJoc/{codiPartida}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void acabarJoc(@PathParam("codiPartida") int codiPartida) {
        if (codiPartida <= partidas.size() && codiPartida > 0) {
            partidas.remove(codiPartida - 1);
            System.out.println("Juego finalizado " + codiPartida);
        } else {
            System.out.println("Código de partida no válido.");
        }
    }
}
