const express = require('express');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.json());

const partides = [];

class Partida {
  constructor(codiPartida) {
    this.codiPartida = codiPartida;
    this.mans = [];
    this.repartirCartes(7);
  }

  repartirCartes(cartesPerJugador) {
    const baralla = crearBaralla();
    shuffle(baralla);

    const numJugadors = 2;
    const cartesTotals = cartesPerJugador * numJugadors;

    if (baralla.length >= cartesTotals) {
      for (let i = 0; i < numJugadors; i++) {
        const maJugador = baralla.slice(i * cartesPerJugador, (i + 1) * cartesPerJugador);
        this.mans.push(maJugador);
      }
    } else {
      console.log("No hay suficientes cartas para repartir a los jugadores.");
    }
  }

  mostrarMaJugador(numJugador) {
    if (numJugador > 0 && numJugador <= this.mans.length) {
      return this.mans[numJugador - 1];
    } else {
      return [];
    }
  }

  tirarCarta(numJugador, carta) {
    if (numJugador > 0 && numJugador <= this.mans.length) {
      const maJugador = this.mans[numJugador - 1];

      const cartaIndex = maJugador.indexOf(carta);
      if (cartaIndex !== -1) {
        maJugador.splice(cartaIndex, 1);
        return `El jugador ${numJugador} ha tirado una carta: ${carta}`;
      } else {
        return `La carta no está en la mano del jugador ${numJugador}`;
      }
    } else {
      return "Número de jugador no válido.";
    }
  }

  afegirMaJugador(maJugador) {
    this.mans.push(maJugador);
  }
}

function crearBaralla() {
  const colors = ["Vermell", "Verd", "Blau", "Groc"];
  const valors = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Salta", "Inverteix", "AgafaDos"];

  const baralla = [];

  for (const color of colors) {
    for (const valor of valors) {
      baralla.push(`${color} ${valor}`);
    }
  }

  return baralla;
}

function shuffle(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

app.post('/api/iniciarJoc/:codiPartida', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  if (!esPartidaValida(codiPartida)) {
    const novaPartida = new Partida(codiPartida);
    partides.push(novaPartida);
    res.send(`Joc iniciat amb èxit. Codi de partida: ${codiPartida}`);
  } else {
    res.send(`El codi de partida ${codiPartida} ja existeix. Si us plau, tria'n un altre.`);
  }
});

app.get('/api/mostrarCartes/:codiPartida/:numJugador', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  const numJugador = parseInt(req.params.numJugador);
  if (esPartidaValida(codiPartida)) {
    res.json(partides[codiPartida - 1].mostrarMaJugador(numJugador));
  } else {
    res.json([]);
  }
});

app.put('/api/tirarCarta/:codiPartida/:carta/:numJugador', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  const carta = req.params.carta;
  const numJugador = parseInt(req.params.numJugador);
  if (esPartidaValida(codiPartida)) {
    res.send(partides[codiPartida - 1].tirarCarta(numJugador, carta));
  } else {
    res.send("Codi de partida no vàlid.");
  }
});

app.put('/api/moureJugador/:codiPartida/passa', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  if (esPartidaValida(codiPartida)) {
    res.send("El jugador ha passat el seu torn.");
  } else {
    res.send("Codi de partida no vàlid.");
  }
});

app.put('/api/moureJugador/:codiPartida/robar/:quantitat', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  const quantitat = parseInt(req.params.quantitat);
  if (esPartidaValida(codiPartida)) {
    res.send(`El jugador ha robat ${quantitat} cartes.`);
  } else {
    res.send("Codi de partida no vàlid.");
  }
});

app.delete('/api/acabarJoc/:codiPartida', (req, res) => {
  const codiPartida = parseInt(req.params.codiPartida);
  if (esPartidaValida(codiPartida)) {
    partides.splice(codiPartida - 1, 1);
    console.log(`Joc finalitzat ${codiPartida}`);
    res.send(`Joc finalitzat ${codiPartida}`);
  } else {
    res.send("Codi de partida no vàlid.");
  }
});

function esPartidaValida(codiPartida) {
  return codiPartida <= partides.length && codiPartida > 0;
}

app.listen(8080, () => {
  console.log(`Servidor en funcionament a http://localhost:8080`);
});
