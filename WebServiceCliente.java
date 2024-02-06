package proyecto.cartas.proyectocartas;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebServiceCliente {

    private static final String URL_BASE = "http://localhost:8080/ProyectoCartas_war_exploded/api/";
    private static final BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void iniciarJoc() throws IOException {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(lector.readLine());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_BASE + "iniciarJoc/" + codiPartida)
                .post(RequestBody.create("", null))
                .build();

        enviarPeticio(client, request);
    }

    private static void mostrarCartes() throws IOException {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(lector.readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(lector.readLine());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_BASE + "mostrarCartes/" + codiPartida + "/" + numJugador)
                .get()
                .build();

        enviarPeticio(client, request);
    }


    private static void tirarCarta() throws IOException {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(lector.readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(lector.readLine());
        System.out.println("Introdueix la carta a tirar: ");
        String carta = lector.readLine();
        System.out.println("Introdueix el nou color (si escau): ");
        String nouColor = lector.readLine();

        carta = carta.replace(" ", "%20");

        String url = URL_BASE + "tirarCarta/" + codiPartida + "/" + carta + "/" + numJugador;

        if (nouColor != null && !nouColor.isEmpty()) {
            nouColor = nouColor.replace(" ", "%20");
            url += "?nuevoColor=" + nouColor;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", null))
                .build();

        enviarPeticio(client, request);
    }

    private static void passarTorn() throws IOException {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(lector.readLine());
        System.out.println("Introdueix el número de jugador: ");
        int numJugador = Integer.parseInt(lector.readLine());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_BASE + "moureJugador/" + codiPartida + "/passa/" + numJugador)
                .put(RequestBody.create("", null))
                .build();

        enviarPeticio(client, request);
    }

    private static void finalitzarJoc() throws IOException {
        System.out.println("Introdueix el codi de la partida: ");
        int codiPartida = Integer.parseInt(lector.readLine());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_BASE + "acabarJoc/" + codiPartida)
                .delete()
                .build();

        enviarPeticio(client, request);
    }

    private static String enviarPeticio(OkHttpClient client, Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            System.out.println("Codi de resposta: " + response.code());
            System.out.println("Resposta: " + response.body().string());
        }
        return null;
    }
}
