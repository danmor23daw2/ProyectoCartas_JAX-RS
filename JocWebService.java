package proyecto.cartas.proyectocartas;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * WebService que representa un joc de cartes de UNO.
 * @version 1.4
 * Autor: Daniel Moreno/ Jose Daniel Sanchez
 * @date 9.11.23
 */
@Path("/")
public class JocWebService {
    private static List<Partida> partides = new ArrayList<>();
    /**
     * Inicia un joc de UNO amb un codi de partida.
     * @param codiPartida Codi de la partida a iniciar
     * @return Missatge indicant l'èxit o l'existència prèvia de la partida
     */
    @POST
    @Path("/iniciarJoc/{codiPartida}")
    @Produces(MediaType.TEXT_PLAIN)
    public String iniciarJoc(@PathParam("codiPartida") int codiPartida) {
        if (!esPartidaValida(codiPartida)) {
            Partida novaPartida = new Partida(codiPartida);
            partides.add(novaPartida);
            return "Joc iniciat amb èxit. Codi de partida: " + codiPartida;
        }
        return "El codi de partida " + codiPartida + " ja existeix. Si us plau, tria'n un altre.";
    }

    private static List<String> crearBaralla() {
        List<String> colors = List.of("Vermell", "Verd", "Blau", "Groc");
        List<String> valors = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Salta", "Inverteix", "AgafaDos");

        List<String> baralla = new ArrayList<>();

        for (String color : colors) {
            for (String valor : valors) {
                baralla.add(color + " " + valor);
            }
        }

        return baralla;
    }

    private void repartirCartes(List<String> baralla, int cartesPerJugador) {
        Collections.shuffle(baralla);

        int numJugadors = 2;
        int cartesTotals = cartesPerJugador * numJugadors;

        if (baralla.size() >= cartesTotals) {
            for (int i = 0; i < numJugadors; i++) {
                List<String> maJugador = new ArrayList<>(baralla.subList(i * cartesPerJugador, (i + 1) * cartesPerJugador));
                partides.get(partides.size() - 1).afegirMaJugador(maJugador);
            }
            baralla.clear();
        } else {
            System.out.println("No hay suficientes cartas para repartir a los jugadores.");
        }
    }
    /**
     * Mostra les cartes d'un jugador en format JSON.
     * @param codiPartida Codi de la partida
     * @param numJugador Número de jugador
     * @return Llista de cartes en format JSON
     */
    @GET
    @Path("/mostrarCartes/{codiPartida}/{numJugador}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> mostrarCartes(@PathParam("codiPartida") int codiPartida,
                                      @PathParam("numJugador") int numJugador) {
        if (esPartidaValida(codiPartida)) {
            return partides.get(codiPartida - 1).mostrarMaJugador(numJugador);
        } else {
            return new ArrayList<>();
        }
    }
    /**
     * Tira una carta en el joc.
     * @param codiPartida Codi de la partida
     * @param carta Carta a ser tirada
     * @param numJugador Número de jugador
     * @return Resposta del joc després de tirar la carta
     */
    @PUT
    @Path("/tirarCarta/{codiPartida}/{carta}/{numJugador}")
    @Produces(MediaType.TEXT_PLAIN)
    public String tirarCarta(@PathParam("codiPartida") int codiPartida,
                             @PathParam("carta") String carta,
                             @PathParam("numJugador") int numJugador) {
        if (esPartidaValida(codiPartida)) {
            return partides.get(codiPartida - 1).tirarCarta(numJugador, carta);
        } else {
            return "Codi de partida no vàlid.";
        }
    }
    /**
     * Mou un jugador en el joc passant el seu torn.
     * @param codiPartida Codi de la partida
     * @return Resposta del joc després de passar el torn
     */
    @PUT
    @Path("/moureJugador/{codiPartida}/passa")
    @Produces(MediaType.TEXT_PLAIN)
    public String moureJugadorPassa(@PathParam("codiPartida") int codiPartida) {
        if (esPartidaValida(codiPartida)) {
            return "El jugador ha passat el seu torn.";
        } else {
            return "Codi de partida no vàlid.";
        }
    }
    /**
     * Mou un jugador en el joc fent una aposta.
     * @param codiPartida Codi de la partida
     * @param quantitat Quantitat apostada
     * @return Resposta del joc després de realitzar el moviment
     */
    @PUT
    @Path("/moureJugador/{codiPartida}/robar/{quantitat}")
    @Produces(MediaType.TEXT_PLAIN)
    public String moureJugador(@PathParam("codiPartida") int codiPartida,
                               @PathParam("quantitat") int quantitat) {
        if (esPartidaValida(codiPartida)) {
            return "El jugador ha robat " + quantitat + " cartes.";
        } else {
            return "Codi de partida no vàlid.";
        }
    }
    /**
     * Finalitza un joc de UNO i elimina la partida.
     * @param codiPartida Codi de la partida a finalitzar
     */
    @DELETE
    @Path("/acabarJoc/{codiPartida}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void acabarJoc(@PathParam("codiPartida") int codiPartida) {
        if (esPartidaValida(codiPartida)) {
            partides.remove(codiPartida - 1);
            System.out.println("Joc finalitzat " + codiPartida);
        } else {
            System.out.println("Codi de partida no vàlid.");
        }
    }

    private boolean esPartidaValida(int codiPartida) {
        return codiPartida <= partides.size() && codiPartida > 0;
    }

    private static class Partida {
        private int codiPartida;
        private List<List<String>> mans;

        public Partida(int codiPartida) {
            this.codiPartida = codiPartida;
            this.mans = new ArrayList<>();
            repartirCartes(7);
        }

        public void repartirCartes(int cartesPerJugador) {
            List<String> baralla = crearBaralla();
            Collections.shuffle(baralla);

            int numJugadors = 2;
            int cartesTotals = cartesPerJugador * numJugadors;

            if (baralla.size() >= cartesTotals) {
                for (int i = 0; i < numJugadors; i++) {
                    List<String> maJugador = new ArrayList<>(baralla.subList(i * cartesPerJugador, (i + 1) * cartesPerJugador));
                    mans.add(maJugador);
                }
                baralla.clear();
            } else {
                System.out.println("No hay suficientes cartas para repartir a los jugadores.");
            }
        }

        public List<String> mostrarMaJugador(int numJugador) {
            if (numJugador > 0 && numJugador <= mans.size()) {
                return mans.get(numJugador - 1);
            } else {
                return new ArrayList<>();
            }
        }

        public String tirarCarta(int numJugador, String carta) {
            if (numJugador > 0 && numJugador <= mans.size()) {
                List<String> maJugador = mans.get(numJugador - 1);

                if (maJugador.contains(carta)) {
                    maJugador.remove(carta);
                    return "El jugador " + numJugador + " ha tirat una carta: " + carta;
                } else {
                    return "La carta no està a la mà del jugador " + numJugador;
                }
            } else {
                return "Número de jugador no vàlid.";
            }
        }

        public void afegirMaJugador(List<String> maJugador) {
            mans.add(maJugador);
        }
    }
}
