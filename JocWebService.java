package proyecto.cartas.proyectocartas;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/")
public class JocWebService {

    private static List<Partida> partides = new ArrayList<>();

    @POST
    @Path("/iniciarJoc/{codiPartida}")
    @Produces(MediaType.TEXT_PLAIN)
    public String iniciarJoc(@PathParam("codiPartida") int codiPartida) {
        if (!esPartidaValida(codiPartida)) {
            Partida novaPartida = new Partida(codiPartida);
            partides.add(novaPartida);
            return "Joc iniciat amb èxit. Codi de partida: " + codiPartida + ". Carta inicial: " + novaPartida.getCartaInicial();
        } else {
            return "El codi de partida " + codiPartida + " ja existeix. Si us plau, tria'n un altre.";
        }
    }

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

    @PUT
    @Path("/tirarCarta/{codiPartida}/{carta}/{numJugador}")
    @Produces(MediaType.TEXT_PLAIN)
    public String tirarCarta(@PathParam("codiPartida") int codiPartida,
                             @PathParam("carta") String carta,
                             @PathParam("numJugador") int numJugador,
                             @QueryParam("nuevoColor") String nuevoColor) {
        if (esPartidaValida(codiPartida)) {
            Partida partida = partides.get(codiPartida - 1);
            System.out.println("Partida actual: " + partida);

            try {
                if (carta.contains("CanviColor") || carta.contains("AgafaQuatre")) {
                    if (nuevoColor == null || !List.of("Vermell", "Verd", "Blau", "Groc").contains(nuevoColor)) {
                        return "Error: El nuevo color proporcionado para la carta CanviColor no es válido. Debe ser uno de: Vermell, Verd, Blau o Groc.";
                    }
                    if (carta.contains("CanviColor")) {
                        partida.establecerCartaInicial(nuevoColor + " CanviColor");
                    }
                }

                String resultadoTirada = partida.tirarCarta(numJugador, carta, nuevoColor);

                if (resultadoTirada.startsWith("Error")) {
                    String cartaEnMesa = partida.getUltimaCarta();
                    System.out.println("Carta en la mesa: " + cartaEnMesa);
                    return resultadoTirada + ". Carta en la mesa: " + cartaEnMesa;
                } else {
                    String mensaje = String.format("El jugador %d ha tirado %s", numJugador, resultadoTirada);
                    System.out.println("Carta Inicial: " + partida.getCartaInicial());
                    System.out.println("Última Carta: " + partida.getUltimaCarta());
                    return mensaje;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: Se produjo una excepción durante la tirada de la carta.";
            }
        } else {
            return "Codi de partida no vàlid.";
        }
    }




    @PUT
    @Path("/moureJugador/{codiPartida}/passa/{numJugador}")
    @Produces(MediaType.TEXT_PLAIN)
    public String moureJugadorPassa(@PathParam("codiPartida") int codiPartida,
                                    @PathParam("numJugador") int numJugador) {
        if (esPartidaValida(codiPartida)) {
            return partides.get(codiPartida - 1).passarTorn(numJugador);
        } else {
            return "Codi de partida no vàlid.";
        }
    }


    @DELETE
    @Path("/acabarJoc/{codiPartida}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String acabarJoc(@PathParam("codiPartida") int codiPartida) {
        if (esPartidaValida(codiPartida)) {
            partides.remove(codiPartida - 1);
            return "Joc finalitzat " + codiPartida;
        } else {
            return "Codi de partida no vàlid.";
        }
    }

    private boolean esPartidaValida(int codiPartida) {
        return codiPartida <= partides.size() && codiPartida > 0;
    }

    private static class Partida {
        private int codiPartida;
        private List<List<String>> mans;
        private String cartaInicial;
        private String ultimaCarta;
        private int turnoActual;
        private int jugadoresQueHanPasado;
        private void establecerCartaInicial() {
            List<String> colores = List.of("Vermell", "Verd", "Blau", "Groc");
            List<String> numeros = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

            List<String> cartasNumerosColores = new ArrayList<>();

            for (String color : colores) {
                for (String numero : numeros) {
                    cartasNumerosColores.add(color + " " + numero);
                }
            }

            Collections.shuffle(cartasNumerosColores);
            this.cartaInicial = cartasNumerosColores.get(0);
        }

        public String getUltimaCarta() {
            return ultimaCarta != null ? ultimaCarta : "No hay carta en la mesa.";
        }

        public Partida(int codiPartida) {
            this.codiPartida = codiPartida;
            this.mans = new ArrayList<>();
            repartirCartes(7);
            this.establecerCartaInicial();
            this.turnoActual = 1;
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
                System.out.println("No hi ha prou cartes per repartir als jugadors.");
            }
        }

        public List<String> mostrarMaJugador(int numJugador) {
            if (numJugador > 0 && numJugador <= mans.size()) {
                return mans.get(numJugador - 1);
            } else {
                return new ArrayList<>();
            }
        }

        public String passarTorn(int numJugador) {
            if (numJugador == turnoActual) {
                turnoActual = turnoActual % 2 + 1;
                jugadoresQueHanPasado = 0;

                List<String> baralla = crearBaralla();
                Collections.shuffle(baralla);

                String cartaRobada = baralla.remove(0);
                mans.get(numJugador - 1).add(cartaRobada);

                return String.format("El torn s'ha passat al jugador %d. El jugador %d ha robat 1 carta.", turnoActual, numJugador);
            } else {
                return "No és el teu torn per passar.";
            }
        }


        public String getCartaInicial() {
            return cartaInicial != null ? cartaInicial : "No s'ha establert cap carta inicial.";
        }

        public String tirarCarta(int numJugador, String carta, String nuevoColor) {
            if (numJugador == 1 || numJugador == 2) {
                List<String> maJugador = mans.get(numJugador - 1);

                if (numJugador == turnoActual) {
                    if (ultimaCarta == null) {
                        if (maJugador.contains(carta) && (carta.contains("CanviColor") || carta.contains("AgafaQuatre") || esMismaCartaInicial(carta))) {
                            maJugador.remove(carta);

                            if (carta.contains("CanviColor")) {
                                if (nuevoColor == null || !List.of("Vermell", "Verd", "Blau", "Groc").contains(nuevoColor)) {
                                    return "Error: Debes proporcionar un nuevo color válido para la carta CanviColor (Vermell, Verd, Blau o Groc).";
                                }

                                cartaInicial = nuevoColor + " CanviColor";
                                ultimaCarta = nuevoColor + " CanviColor";
                                turnoActual = turnoActual % 2 + 1;

                                return "El jugador " + numJugador + " ha tirat " + ultimaCarta + " i ha triat el color " + nuevoColor + ".";
                            } else if (carta.contains("AgafaQuatre")) {
                                int jugadorOponente = turnoActual % 2 + 1;

                                for (int i = 0; i < 4; i++) {
                                    mans.get(jugadorOponente - 1).add(generarCartaRandom());
                                }

                                ultimaCarta = nuevoColor + " AgafaQuatre";
                                turnoActual = turnoActual % 2 + 1;

                                return "El jugador " + numJugador + " ha tirat " + ultimaCarta + ". El jugador " + jugadorOponente + " ha robado 4 cartas.";
                            }
                            if (carta.contains("AgafaDos")) {
                                int jugadorSiguiente = turnoActual % 2 + 1;

                                for (int i = 0; i < 2; i++) {
                                    mans.get(jugadorSiguiente - 1).add(generarCartaRandom());
                                }

                                jugadoresQueHanPasado = 2;
                                turnoActual = numJugador;

                                return "El jugador " + numJugador + " ha tirat " + carta + ". El jugador " + jugadorSiguiente + " ha robat 2 cartes. No pot tirar al següent torn.";
                            } else if (carta.contains("Salta")) {
                                turnoActual = numJugador;
                                ultimaCarta = carta;
                                return "El jugador " + numJugador + " ha tirat " + carta + ". Ha saltat el torn del jugador " + (numJugador % 2 + 1) + ".";
                            } else if (carta.contains("Inverteix")) {
                                turnoActual = numJugador;
                                ultimaCarta = carta;
                                return "El jugador " + numJugador + " ha tirat " + carta + ". S'ha invertit el torn. Ara li toca al jugador " + numJugador + ".";
                            } else {
                                turnoActual = turnoActual % 2 + 1;
                            }

                            ultimaCarta = carta;

                            return "El jugador " + numJugador + " ha tirat una carta: " + carta;
                        } else {
                            return "La carta no està a la mà del jugador " + numJugador + " o no es pot tirar. Carta a la taula: " + cartaInicial + ".";
                        }
                    } else {
                        if (puedeTirarCarta(carta)) {
                            int cartaIndex = maJugador.indexOf(carta);
                            if (cartaIndex != -1) {
                                maJugador.remove(cartaIndex);

                                if (carta.contains("AgafaDos") || carta.contains("Salta") || carta.contains("Inverteix")) {
                                } else {
                                    ultimaCarta = carta;
                                }

                                if (carta.contains("AgafaDos")) {
                                    int jugadorSiguiente = turnoActual % 2 + 1;

                                    for (int i = 0; i < 2; i++) {
                                        mans.get(jugadorSiguiente - 1).add(generarCartaRandom());
                                    }

                                    jugadoresQueHanPasado = 2;
                                    turnoActual = numJugador;

                                    return "El jugador " + numJugador + " ha tirat " + carta + ". El jugador " + jugadorSiguiente + " ha robat 2 cartes. No pot tirar al següent torn.";
                                } else if (carta.contains("Salta")) {
                                    turnoActual = numJugador;
                                    ultimaCarta = carta;
                                    return "El jugador " + numJugador + " ha tirat " + carta + ". Ha saltat/bloquejat el torn del jugador " + (numJugador % 2 + 1) + ".";
                                } else if (carta.contains("Inverteix")) {
                                    turnoActual = numJugador;
                                    ultimaCarta = carta;
                                    return "El jugador " + numJugador + " ha tirat " + carta + ". S'ha invertit el torn. Ara li toca al jugador " + numJugador + ".";
                                } else {
                                    turnoActual = turnoActual % 2 + 1;
                                }

                                ultimaCarta = carta;

                                return "El jugador " + numJugador + " ha tirat una carta: " + carta;
                            } else {
                                return "La carta no està a la mà del jugador " + numJugador;
                            }
                        } else {
                            return "Error: Has de tirar una carta que tingui el mateix color, el mateix número o una carta especial ('Salta', 'Inverteix', 'AgafaDos').";
                        }
                    }
                } else {
                    return "No és el teu torn per tirar.";
                }
            } else {
                return "Número de jugador no válido.";
            }
        }


        private boolean esMismaCartaInicial(String carta) {
            String[] inicial = cartaInicial.split(" ");
            String[] actual = carta.split(" ");

            return actual[0].equals(inicial[0]) || actual[1].equals(inicial[1]);
        }

        private boolean puedeTirarCarta(String carta) {
            if (ultimaCarta == null) {
                return true;
            }

            String[] ultima = ultimaCarta.split(" ");
            String[] actual = carta.split(" ");

            if ("Salta".equals(actual[1]) || "Inverteix".equals(actual[1]) || "AgafaDos".equals(actual[1])) {
                return actual[0].equals(ultima[0]) || actual[1].equals(ultima[1]);
            }

            return actual[0].equals(ultima[0]) || actual[1].equals(ultima[1]);
        }

        private String generarCartaRandom() {
            List<String> baralla = crearBaralla();
            Collections.shuffle(baralla);
            return baralla.remove(0);
        }

        private List<String> crearBaralla() {
            List<String> colors = List.of("Vermell", "Verd", "Blau", "Groc");
            List<String> valors = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Salta", "Inverteix", "AgafaDos", "CanviColor", "AgafaQuatre");

            List<String> baralla = new ArrayList<>();

            for (String valor : valors) {
                if ("CanviColor".equals(valor) || "AgafaQuatre".equals(valor)) {
                    baralla.add(valor);
                } else {
                    for (String color : colors) {
                        baralla.add(color + " " + valor);
                    }
                }
            }

            return baralla;
        }

        public void establecerCartaInicial(String s) {
        }
    }
}
