<template>
  <template v-if="isAuthenticated">
    <header3/>
  </template>
  <template v-else>
    <cabecera-componente/>
  </template>
  <div class="container1">
    <div class="container-principal">
      <div class="datos">
        <h1>{{ establecimiento.nombre }}</h1>
        <p>Teléfono: {{ establecimiento.telefono }}</p>
        <p>Localidad: {{ establecimiento.localidad }}</p>
        <p>Provincia: {{ establecimiento.provincia }}</p>
        <p>Calle: {{ establecimiento.calle }}</p>
        <p>Código Postal: {{ establecimiento.codPostal }}</p>
        <p>País: {{ establecimiento.pais }}</p>
        <p>
          <i v-if="isAuthenticated" class="far fa-thumbs-up like-icon" @click="likeEstablecimiento(establecimiento.idEstablecimiento)">
            {{ establecimiento.numLikes}}
          </i>
          <span v-else>
            <strong>Debe iniciar sesión para dar like y marcar este establecimiento como favorito y/o visitado.</strong>
          </span>
        </p>
        <button v-if="isAuthenticated && !esFavorito" @click="marcarComoFavorito()" class="boton-subir">
          Marcar como favorito
        </button>
        <button v-if="isAuthenticated && esFavorito" @click="quitarComoFav()" class="boton-subir">
          Quitar como favorito
        </button>

        <button v-if="isAuthenticated && !esVisitado" @click="marcarComoVisitado()" class="boton-subir">
          Marcar como visitado
        </button>
        <button v-if="isAuthenticated && esVisitado" @click="eliminarVis()" class="boton-subir">
          Quitar como visitado
        </button>

        <button v-if="rol === 'ADMIN'" @click="toggleEditarEstablecimiento" class="boton-subir2">Editar establecimiento</button> <!-- Botón de edición para administradores -->
        <button v-if="rol === 'ADMIN'" @click="eliminarEstablecimiento" class="boton-subir2">Eliminar establecimiento</button> <!-- Botón de eliminación para administradores -->

        <div v-if="mostrarFormularioEditar" class="formulario-edicion">
          <h2>Editar Establecimiento</h2>
          <form @submit.prevent="editarEstablecimiento">
            <div>
              <label for="nombre">Nombre del Establecimiento:</label>
              <input type="text" v-model="formulario.nombre" id="nombre" required>
            </div>
            <div>
              <label for="telefono">Teléfono:</label>
              <input type="text" v-model="formulario.telefono" id="telefono" required>
            </div>
            <div>
              <label for="calle">Calle:</label>
              <input type="text" v-model="formulario.calle" id="calle" required>
            </div>
            <div>
              <label for="codPostal">Código Postal:</label>
              <input type="text" v-model="formulario.codPostal" id="codPostal" required>
            </div>
            <button type="submit" class="boton-subir">Guardar cambios</button>
            <button type="button" @click="cancelarEdicion" class="boton-subir2">Cancelar</button>
          </form>
        </div>
      </div>
      <div class="mapa" ref="map"></div>
      <div class="imagenes">
        <div class="galeria">
          <img v-for="(imagen, index) in imagenes" :key="index" :src="imagen" alt="Imagen" @click="mostrarImagen(index)">
        </div>
        <input type="file" ref="fileInput" style="display: none" @change="onFileChange">
        <button v-if="isAuthenticated" @click="openFileInput" class="boton-subir">¿Dispone de alguna imagen de este establecimiento? ¡Compártela con nosotros!</button>
        <p>
          Valoración media:
          <span v-for="(estrella, index) in calcularEstrellas()" :key="index">
            <i v-if="estrella === 'full'" class="fas fa-star star-yellow star-size"></i>
            <i v-if="estrella === 'half'" class="fas fa-star-half-alt star-yellow star-size"></i>
            <i v-if="estrella === 'empty'" class="far fa-star star-yellow star-size"></i>
          </span>
          (Calculada como la media entre el número de likes y los usuarios que han visitado este establecimiento.)
        </p>


      </div>
      <!-- Modal para mostrar la imagen en tamaño completo -->
      <div class="modal" v-if="imagenSeleccionada !== null">
        <div class="modal-contenido">
          <span class="eliminar" @click="eliminarImagen(imagenSeleccionada)">
            <i class="fas fa-trash-alt"></i>
          </span>
          <span class="cerrar" @click="cerrarModal">&times;</span>
          <img :src="imagenSeleccionada" alt="Imagen" style="max-width: 100%; max-height: 100%;">
        </div>
      </div>
    </div>
    <div class="comentarios">
      <div class="titulo">
        <h2> Comentarios </h2>
      </div>
      <div v-if="isAuthenticated" class="nuevo-comentario">
        <div class="campo-comentario-con-boton">
          <textarea v-model="nuevoComentario" placeholder="Introduce tu comentario (máximo 140 caracteres)" maxlength="140" class="campo-comentario"></textarea>
          <button @click="enviarComentario" class="boton-subir">Enviar</button>
        </div>
      </div>
      <div v-for="(comentario, index) in comentarios" :key="index" class="comentario">
        <div class="comentario-contenido">
          <p><strong>{{comentario.autor.username}}</strong> Comentó: {{comentario.mensaje}}</p>
          <p>{{comentario.fecha}}</p>
          <div class="acciones">
            <i v-if="comentario.autor.username === usuarioActual" class="fas fa-trash-alt fa-lg" @click="eliminarComentario(comentario.id)"></i>
          </div>
          <div v-if="respuestasAbiertas.includes(index)" class="nueva-respuesta">
            <div class="campo-comentario-con-boton">
              <textarea v-model="nuevaRespuesta[index]" placeholder="Introduce tu respuesta (máximo 140 caracteres)" maxlength="140" class="campo-comentario"></textarea>
              <button @click="enviarRespuesta(index, comentario.id)" class="boton-subir">Enviar</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <contacto/>
  <footer-componente/>
</template>

<script>
import Header3 from "@/components/headerIniciadoSesion.vue";
import FooterComponente from "@/components/footer.vue";
import CabeceraComponente from "@/components/header.vue";
import axios from "axios";
import 'ol/ol.css';
import { loadModules } from 'esri-loader';
import {mapGetters} from "vuex";
import contacto from "@/components/contacto.vue";

export default {
  name: 'Vista-Establecimiento',
  components: {
    CabeceraComponente,
    FooterComponente,
    Header3,
    contacto
  },
  data() {
    return {
      establecimiento: '',
      imagenes: [],
      imagenSeleccionada: null,
      map: null,
      comentarios: [],
      usuarioActual: '',
      nuevoComentario: '',
      sePuedeResponder: false,
      respuestasAbiertas: [],
      nuevaRespuesta: {},
      favoritosUsuario: [],
      visitadosUsuario: [],
      esFavorito: false,
      esVisitado: false,
      rol: '',
      mostrarFormularioEditar: false,
      formulario: {
        nombre:'',
        telefono: '',
        localidad: '',
        provincia: '',
        calle: '',
        codPostal: '',
        pais: ''
      },
      valoracionMedia:'',
      mensajeEnviado: false

    };
  },
  mounted() {
    this.obtenerDatosEstablecimiento();
    this.$nextTick(() => {
      this.mostrarMapa();
    });
    this.cargarComentarios();
    this.usuarioActual = this.username;
    this.obtenerFavoritosUsuario();
    this.obtenerVisitadosUsuario();
    this.obtenerDatosUsuario();

  },
  computed: {
    ...mapGetters(['username', 'isAuthenticated'])
  },
  methods: {
    handleSubmit() {
      if(!this.validateForm()){
        return;
      }
      axios.post('http://localhost:8080/ociosingluten/quejas/nuevaQueja', {
        nombre: this.name,
        email: this.email,
        mensaje: this.message
      })
          .then(response => {
            console.log('Mensaje enviado con éxito:', response.data);
            this.mensajeEnviado = true;
          })
          .catch(error => {
            console.error('Error al enviar el mensaje:', error);
          });
    },
    validateForm() {
      if (!this.name || !this.email || !this.message) {
        alert('Todos los campos son obligatorios.');
        return false;
      }
    },
    async obtenerDatosEstablecimiento() {
      const id = this.$route.params.idEstablecimiento;
      const response = await axios.get(`http://localhost:8080/ociosingluten/establecimientos/establecimiento/${id}`);
      this.establecimiento = response.data;
      this.imagenes = this.establecimiento.imagenesBase64.map(base64 => 'data:image/png;base64,' + base64);
      if(this.establecimiento.numLikes < this.establecimiento.visitantes.length) {
        this.valoracionMedia = 5 * (this.establecimiento.numLikes / this.establecimiento.visitantes.length);
      }else{
        this.valoracionMedia = 5 * (this.establecimiento.visitantes.length / this.establecimiento.numLikes);
      }
    },
    async obtenerFavoritosUsuario() {
      if (!this.isAuthenticated) {
        return;
      }
      const username = this.username;
      const response1 = await axios.get(`http://localhost:8080/ociosingluten/usuarios/perfilUsuario/${username}/estFavoritos`);
      this.favoritosUsuario = response1.data;
      const id = this.$route.params.idEstablecimiento;
      for (let i = 0; i < this.favoritosUsuario.length; i++) {
        if(parseInt(this.favoritosUsuario[i].idEstablecimiento) === parseInt(id)){
          this.esFavorito = true;
          break;
        }
      }
    },
    async obtenerVisitadosUsuario() {
      if (!this.isAuthenticated) {
        return;
      }
      const username = this.username;
      const response2 = await axios.get(`http://localhost:8080/ociosingluten/usuarios/perfilUsuario/${username}/estVisitados`);
      this.visitadosUsuario = response2.data;
      const id = this.$route.params.idEstablecimiento;
      for (let i = 0; i < this.visitadosUsuario.length; i++) {
        if(parseInt(this.visitadosUsuario[i].idEstablecimiento) === parseInt(id)){
          this.esVisitado = true;
          break;
        }
      }
    },
    async obtenerDatosUsuario(){
      if (!this.isAuthenticated) {
        return;
      }
      const yo = this.username;
      const response2 = await axios.get(`http://localhost:8080/ociosingluten/usuarios/perfilUsuarioUsername/${yo}`);
      this.rol = response2.data.rol;
    },
    openFileInput() {
      if (this.isAuthenticated) {
        this.$refs.fileInput.click();
      } else {
        this.$router.push('/iniciaSesion');
      }
    },
    calcularEstrellas() {
        const valoracionMedia = this.valoracionMedia;
        const estrellasLlenas = Math.floor(valoracionMedia);
        const estrellaMedia = valoracionMedia - estrellasLlenas;
        let estrellas = [];

        for (let i = 0; i < estrellasLlenas; i++) {
          estrellas.push('full');
        }

        if (estrellaMedia >= 0.5) {
          estrellas.push('half');
        }

        const estrellasRestantes = 5 - estrellas.length;
        for (let i = 0; i < estrellasRestantes; i++) {
          estrellas.push('empty');
        }

        return estrellas;
      },
    enviarComentario() {
      if (this.nuevoComentario.trim() === '') {
        return;
      }
      const id = this.$route.params.idEstablecimiento;
      const nuevoComentarioDTO = {
        username: this.usuarioActual,
        mensaje: this.nuevoComentario
      };
      axios.post(`http://localhost:8080/ociosingluten/establecimientos/establecimientos/${id}/nuevoComentario`, nuevoComentarioDTO)
          .then(() => {
            this.cargarComentarios();
            this.nuevoComentario = '';
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    async onFileChange(event) {
      const file = event.target.files[0];
      const id = this.$route.params.idEstablecimiento;
      if (file) {
        const reader = new FileReader();
        reader.onload = async (e) => {
          const fotoPerfilBase64 = e.target.result.split(',')[1];
          try {
            await axios.post(`http://localhost:8080/ociosingluten/establecimientos/establecimientoFoto/${id}/nuevaFoto`, fotoPerfilBase64, {
              headers: {
                'Content-Type': 'text/plain'
              }
            });
            await this.obtenerDatosEstablecimiento();
          } catch (error) {
            console.error('Error al actualizar la foto de perfil:', error);
          }
        };
        reader.readAsDataURL(file);
      }
    },
    mostrarImagen(index) {
      this.imagenSeleccionada = this.imagenes[index];
    },
    cerrarModal() {
      this.imagenSeleccionada = null;
    },
    async eliminarImagen(imagenSeleccionada){
      const id = this.$route.params.idEstablecimiento;
      console.log(imagenSeleccionada.id);
      try {
        const base64Image = imagenSeleccionada.split(',')[1];

        const response = await fetch(`http://localhost:8080/ociosingluten/establecimientos/establecimientoFoto/${id}/fotomenos`, {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain'
          },
          body: base64Image
        });
        if (response.ok) {
          console.log("Imagen eliminada correctamente");
          this.cerrarModal();
          location.reload();
        } else {
          console.error("Error al eliminar la imagen:", response.statusText);
        }
      } catch (error) {
        console.error("Error inesperado:", error);
      }
    },
    async obtenerCoordenadas() {
      try {
        const id = this.$route.params.idEstablecimiento;
        const response1 = await axios.get(`http://localhost:8080/ociosingluten/establecimientos/establecimiento/${id}`);
        this.establecimiento = response1.data;
        const direccion = `${this.establecimiento.calle}, ${this.establecimiento.localidad}, ${this.establecimiento.provincia}, ${this.establecimiento.codPostal}, ${this.establecimiento.pais}`;
        const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(direccion)}&format=json`;

        const response = await axios.get(url);
        if (response.data && response.data.length > 0) {
          const resultado = response.data[0];
          const latitud = parseFloat(resultado.lat);
          const longitud = parseFloat(resultado.lon);
          return [latitud, longitud];
        } else {
          console.error('No se encontraron resultados para la dirección proporcionada.');
        }
      } catch (error) {
        console.error('Error al obtener coordenadas:', error);
      }
    },
    async cargarComentarios() {
      try {
        const id = this.$route.params.idEstablecimiento;
        const response = await axios.get(`http://localhost:8080/ociosingluten/establecimientos/establecimientos/${id}/comentarios`);
        this.comentarios = response.data;
      } catch (error) {
        console.error('Error al cargar los comentarios:', error);
      }
    },
    async mostrarMapa() {
      try {
        const coordenadas = await this.obtenerCoordenadas();

        // Carga los módulos necesarios de ArcGIS API
        const [Map, MapView, Graphic, Point] = await loadModules([
          'esri/Map',
          'esri/views/MapView',
          'esri/Graphic',
          'esri/geometry/Point'
        ]);

        // Crea una instancia del mapa
        const map = new Map({
          basemap: 'streets-navigation-vector'
        });

        // Crea una instancia de MapView y asigna el mapa
        const view = new MapView({
          container: this.$refs.map,
          map,
          center: [coordenadas[1], coordenadas[0]], // Invierte las coordenadas para ArcGIS API
          zoom: 14,
          ui: {
            components: []
          }
        });

        // Desactiva la atribución
        view.attributionVisible = false;

        // Crea un punto gráfico para la ubicación
        const point = new Point({
          longitude: coordenadas[1],
          latitude: coordenadas[0]
        });

        // Crea un gráfico para mostrar el punto en el mapa
        const graphic = new Graphic({
          geometry: point,
          symbol: {
            type: 'simple-marker',
            color: 'yellow',
            size: '12px'
          }
        });

        // Añade el gráfico al mapa
        view.graphics.add(graphic);
      } catch (error) {
        console.error('Error al mostrar el mapa:', error);
      }
    },
    async marcarComoFavorito(){
      const id = this.$route.params.idEstablecimiento;
      const username = this.username;
      axios.post(`http://localhost:8080/ociosingluten/establecimientos/${id}/favorito`, username)
          .then(() => {
            this.cargarComentarios();
            this.esFavorito = true;
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    async marcarComoVisitado(){
      const id = this.$route.params.idEstablecimiento;
      const username = this.username;
      axios.post(`http://localhost:8080/ociosingluten/establecimientos/${id}/visitado`, username)
          .then(() => {
            this.cargarComentarios();
            this.esVisitado = true;
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    async quitarComoFav() {
      try {
        const id = this.$route.params.idEstablecimiento;
        const username = this.username;
        const response = await axios.delete(`http://localhost:8080/ociosingluten/establecimientos/${id}/nofavorito`, {
          params: {
            username: username
          }
        });
        if (response.status === 200) {
          this.esFavorito = false;
          console.log('Establecimiento quitado como favorito con éxito.');
        } else {
          console.error('Error al quitar el establecimiento como favorito:', response.data);
        }
      } catch (error) {
        console.error('Error al quitar el establecimiento como favorito:', error);
      }
    },
    async eliminarVis() {
      try {
        const id = this.$route.params.idEstablecimiento;
        const username = this.username;
        const response = await axios.delete(`http://localhost:8080/ociosingluten/establecimientos/${id}/noVisitado`, {
          params: {
            username: username
          }
        });
        if (response.status === 200) {
          this.esVisitado = false;
          console.log('Establecimiento quitado como favorito con éxito.');
        } else {
          console.error('Error al quitar el establecimiento como favorito:', response.data);
        }
      } catch (error) {
        console.error('Error al quitar el establecimiento como favorito:', error);
      }
    },
    async enviarRespuesta(index, id) {
      // Verifica si la respuesta no está vacía
      const respuesta = this.nuevaRespuesta[index];
      if (respuesta.trim() === '') {
        return;
      }
      const nuevoComentarioDTO = {
        username: this.username,
        mensaje: respuesta
      };

      axios.post(`http://localhost:8080/ociosingluten/comentario/${id}/nuevaRespuesta`, nuevoComentarioDTO)
          .then(() => {
            this.cargarComentarios();
            this.nuevaRespuesta[index] = '';
            this.toggleRespuesta(index);
          })
          .catch(error => {
            console.error('Error al enviar la respuesta:', error);
          });
    },
    async eliminarComentario(id) {
      const username = this.username;
      const idEstablecimiento = this.$route.params.idEstablecimiento;
      const url = `http://localhost:8080/ociosingluten/comentario/${id}?username=${username}&idEstablecimiento=${idEstablecimiento}`;

      axios.delete(url)
          .then(() => {
            this.cargarComentarios();
          })
          .catch(error => {
            console.error('Error al enviar la solicitud:', error);
          });
    },
    async likeEstablecimiento(id) {
      try {
        const response = await axios.post(`http://localhost:8080/ociosingluten/establecimientos/${id}/nuevoLike`, this.username);
        if (response.status === 202) {
          this.quitarLike(id);
        } else if (response.status === 200) {
          location.reload();
        }
      } catch (error) {
        alert('Error al enviar el like:', error);
      }
    },
    async quitarLike(id) {
      try {
        const username = this.username;
        const response = await axios.post(`http://localhost:8080/ociosingluten/establecimientos/${id}/likemenos`, username);
        if (response.status === 200) {
          console.log('Like quitado con éxito.');
          location.reload();
        } else {
          console.error('Error al quitar el like:', response.data);
        }
      } catch (error) {
        console.error('Error al quitar el like:', error);
      }
    },
    toggleEditarEstablecimiento() {
      this.formulario.nombre = this.establecimiento.nombre;
      this.formulario.telefono = this.establecimiento.telefono;
      this.formulario.localidad = this.establecimiento.localidad;
      this.formulario.provincia = this.establecimiento.provincia;
      this.formulario.calle = this.establecimiento.calle;
      this.formulario.codPostal = this.establecimiento.codPostal;
      this.formulario.pais = this.establecimiento.pais;
      this.mostrarFormularioEditar = !this.mostrarFormularioEditar;
    },
    cancelarEdicion() {
      this.mostrarFormularioEditar = !this.mostrarFormularioEditar;
    },
    async editarEstablecimiento() {
      if (!this.validarFormulario()) {
        console.error('Error en la validación del formulario');
        return;
      }

      const id = this.$route.params.idEstablecimiento;
      const requestData = {
        nombre: this.formulario.nombre,
        telefono: this.formulario.telefono,
        localidad: this.formulario.localidad,
        provincia: this.formulario.provincia,
        calle: this.formulario.calle,
        codPostal: this.formulario.codPostal,
        pais: this.formulario.pais
      };

      try {
        const response = await axios.put(
            `http://localhost:8080/ociosingluten/establecimientos/establecimientos/${id}/modEstablecimiento`,
            requestData,
            {
              headers: {
                'Content-Type': 'application/json'
              }
            }
        );
        if (response.status === 200) {
          this.establecimiento = response.data;
          this.mostrarFormularioEditar = false;
          this.obtenerDatosEstablecimiento();
        }
      } catch (error) {
        console.error('Error al editar el establecimiento:', error);
      }
    },
    validarFormulario() {
      if (this.formulario.nombre.length > 0 && this.formulario.nombre.length <= 30) {
        this.nombreValido = this.formulario.nombre.length > 0 && this.formulario.nombre.length <= 30;
      }else{
        alert("Nombre incorrecto");
      }

      if (/^\d{9}$/.test(this.formulario.telefono)) {
        this.telefonoValido = /^\d{9}$/.test(this.formulario.telefono);
      }else{
        alert("Telefono incorrecto");
      }

      if(this.formulario.localidad.length > 0) {
        this.localidadValida = this.formulario.localidad.length > 0;
      }else{
        alert("Localidad incorrecta");
      }

      if(this.formulario.provincia.length > 0) {
        this.provinciaValida = this.formulario.provincia.length > 0;
      }else{
        alert("Provincia incorrecta");
      }

      if(this.formulario.calle.length > 0) {
        this.calleValida = this.formulario.calle.length > 0;
      }else{
        alert("Calle incorrecta");
      }

      if(/^\d{5}$/.test(this.formulario.codPostal)) {
        this.codPostalValido = /^\d+$/.test(this.formulario.codPostal);
      }else{
        alert("Codigo postal incorrecto");
      }

      return (
          this.nombreValido &&
          this.telefonoValido &&
          this.localidadValida &&
          this.provinciaValida &&
          this.calleValida &&
          this.codPostalValido &&
          this.paisValido
      );
    },
    eliminarEstablecimiento() {
      if (confirm("¿Desea confirmar la eliminación del establecimiento?")) {
        // Llama al método para eliminar el establecimiento si el usuario confirma
        this.confirmarEliminarEstablecimiento();
      } else {
        console.log("Eliminación cancelada");
      }
    },
    confirmarEliminarEstablecimiento() {
      const id = this.$route.params.idEstablecimiento;
      axios.delete(`http://localhost:8080/ociosingluten/establecimientos/establecimientos/${id}`)
          .then(() => {
            console.log("Establecimiento eliminado con éxito");
          })
          .catch(error => {
            console.error("Error al eliminar el establecimiento:", error);
          });
      this.$router.push('/establecimientos');
    },

  }
}
</script>

<style scoped>
.container-principal {
  display: flex;
  justify-content: space-between;
  width: 75vw;
  padding: 45px;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0px 0px 20px 0px rgba(0,0,0,0.1);
  border: 1px solid #ccc;
  margin: auto;
  margin-top: 20px;

}

.comentarios {
  margin: auto;
  display: ruby-base;
  justify-content: center;
  width: 75vw;
  padding: 45px;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0px 0px 20px 0px rgba(0,0,0,0.1);
  border: 1px solid #ccc;
  margin-top: 20px;
  margin-bottom: 20px;
}

.comentario {
  margin-bottom: 20px;
}

.comentario-contenido {
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
}

.acciones {
  margin-top: 10px;
}

.acciones i {
  margin-right: 10px;
  cursor: pointer;
}

.datos {
  width: 45%;
}

.imagenes {
  width: 45%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.boton-subir {
  margin-top: 10px;
  padding: 8px 12px;
  border-radius: 20px;
  color: white;
  background-color: #9DD9D2;
  border: none;
  cursor: pointer;
}

.boton-subir:hover {
  background-color: #ffcc74;
}

.boton-subir2 {
  margin-top: 10px;
  padding: 8px 12px;
  border-radius: 20px;
  color: white;
  background-color: #9DD9D2;
  border: none;
  cursor: pointer;
}

.boton-subir2:hover {
  background-color: #ff0000;
}

.galeria {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  grid-gap: 10px;
}

.galeria img {
  max-width: 100%;
  max-height: 150px;
  width: auto;
  cursor: pointer;
}


.modal {
  display: block;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  overflow: auto;
}

.modal-contenido {
  background-color: #fefefe;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
  max-width: 800px;
  text-align: center;
}

.cerrar {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.cerrar:hover,
.cerrar:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}


.mapa {
  width: 100%;
  height: 50vh;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  margin-left: 30px;
  margin-right: 20px;

}

.nuevo-comentario {
  margin-bottom: 20px;
}

.campo-comentario-con-boton {
  display: flex;
  align-items: center;
}

.campo-comentario {
  flex: 1;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  margin-right: 10px;
}

.formulario-edicion {
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 20px;
  margin-top: 20px;
}

.formulario-edicion h2 {
  margin-bottom: 15px;
}

.formulario-edicion div {
  margin-bottom: 10px;
}

.formulario-edicion label {
  display: block;
  margin-bottom: 5px;
}

.formulario-edicion input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.formulario-edicion button {
  padding: 10px 20px;
  border-radius: 20px;
  color: white;
  background-color: #9DD9D2;
  border: none;
  cursor: pointer;
}

.formulario-edicion button:hover {
  background-color: #ffcc74;
}

.star-yellow {
  color: #ffcc74;
}

.star-size {
  font-size: 20px;
}


.mensaje-enviado h2 {
  color: white;
  text-align: center;
}

.container1 {
  background-image: url("@/assets/images/_01d90abf-9b74-4813-b728-42c7b8f918a7.jpg");
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-repeat: no-repeat;
  background-size: cover;
  padding: 20px;
}

@media (max-width: 768px) {
  .container-principal {
    flex-direction: column;
  }

  .mapa {
    margin-top: 10px;
  }

  .datos,
  .imagenes {
    width: 100%;
    margin-right: 0;
  }
}

</style>