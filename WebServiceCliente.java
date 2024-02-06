package proyecto.cartas.proyectocartas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class WebServiceCliente {

    private static final String URL_BASE = "http://localhost:8080/ProyectoCartas_war_exploded/api/";

    public static void main(String[] args) {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
            boolean salir = false;

            do {
                System.out.println("Escull una acció:");
                System.out.println("1. Iniciar Joc");
                System.out.println("2. Mostrar Cartes");
                System.out.println("3. Tirar Carta");
                System.out.println("4. Passar Torn");
                System.out.println("5. Finalitzar Joc");
                System.out.println("0. Sortir");

                int opcio = Integer.parseInt(lector.readLine());

                switch (opcio) {
                    case 1:
                        iniciarJoc();
                        break;
                    case 2:
                        mostrarCartes();
                        break;
                    case 3:
                        tirarCarta();
                        break;
                    case 4:
                        passarTorn();
                        break;
                    case 5:
                        finalitzarJoc();
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opció no vàlida");
                }

            } while (!salir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void iniciarJoc() throws Exception {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "iniciarJoc/" + codiPartida))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        enviarPeticio(request);
    }

    private static void mostrarCartes() throws Exception {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "mostrarCartes/" + codiPartida + "/" + numJugador))
                .GET()
                .build();

        enviarPeticio(request);
    }

    private static void tirarCarta() throws Exception {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        System.out.println("Introdueix la carta a tirar: ");
        String carta = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println("Introdueix el nou color (si escau): ");
        String nouColor = new BufferedReader(new InputStreamReader(System.in)).readLine();

        carta = carta.replace(" ", "%20");

        String url = URL_BASE + "tirarCarta/" + codiPartida + "/" + carta + "/" + numJugador;

        if (nouColor != null && !nouColor.isEmpty()) {
            nouColor = nouColor.replace(" ", "%20");
            url += "?nuevoColor=" + nouColor;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        enviarPeticio(request);
    }

    private static void passarTorn() throws Exception {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "moureJugador/" + codiPartida + "/passa/" + numJugador))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        enviarPeticio(request);
    }

    private static void finalitzarJoc() throws Exception {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "acabarJoc/" + codiPartida))
                .DELETE()
                .build();

        enviarPeticio(request);
    }

    private static void enviarPeticio(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Codi de resposta: " + response.statusCode());
        System.out.println("Resposta: " + response.body());
    }

}
