<template>
  <cabecera-componente2/>
  <div class="container1">
    <div class="container2">
      <h1 class="text-center title">¡Pasa a formar parte de Ocio Sin Gluten!</h1>
      <p class="subtitle text-center">¡Introduzca sus datos para pasar a formar parte de la comunidad celiaca!</p>
      <form @submit.prevent="agregarUsuario" class="row">
        <div class="col-md-4">
          <div class="form-group">
            <label for="dni">DNI:</label>
            <input type="text" class="form-control" id="dni" v-model="nuevoUsuario.dni" @change="validarDni" required>
            <span v-if="!dniValido" class="text-danger">DNI no válido.</span>
          </div>
          <div class="form-group">
            <label for="username">Nombre de Usuario:</label>
            <input type="text" class="form-control" id="username" v-model="nuevoUsuario.username" required>
          </div>
          <div class="form-group">
            <label for="nombre">Nombre:</label>
            <input type="text" class="form-control" id="nombre" v-model="nuevoUsuario.nombre" required>
          </div>
          <div class="form-group">
            <label for="apellidos">Apellidos:</label>
            <input type="text" class="form-control" id="apellidos" v-model="nuevoUsuario.apellidos" required>
          </div>
          <div class="form-group">
            <label for="fechaNacimiento">Fecha de Nacimiento:</label>
            <input type="date" class="form-control" id="fechaNacimiento" v-model="nuevoUsuario.fechaNacimiento" @change="validarEdad" required>
            <span v-if="!mayorDeEdad" class="text-danger">Debes ser mayor de edad para registrarte.</span>
          </div>
        </div>
        <div class="col-md-4">
          <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" class="form-control" id="email" v-model="nuevoUsuario.email" @input="validarEmail" required>
            <span v-if="!emailValido" class="text-danger">Email no válido.</span>
          </div>
          <div class="form-group">
            <label for="telefono">Teléfono:</label>
            <input type="text" class="form-control" id="telefono" v-model="nuevoUsuario.telefono" @change="validarTelefono" required>
            <span v-if="!telefonoValido" class="text-danger">Teléfono no válido.</span>
          </div>
          <div class="form-group position-relative">
            <label for="password">Contraseña:</label>
            <input
                :type="showPassword? 'text' : 'password'"
                class="form-control"
                id="password"
                v-model="nuevoUsuario.password"
                @change="validarPassword"
                required
            >
            <button
                type="button"
                class="btn btn-secondary position-absolute end-0 top-50 translate-middle-y show-password-btn"
                style="width: 40px; height: 40px; padding: 0;"
                @click="showPassword =!showPassword"
            >
              <i :class="showPassword? 'fa fa-eye-slash' : 'fa fa-eye-slash'" aria-hidden="true"></i>
            </button>
            <span v-if="!passwordValida" class="text-danger">Contraseña incorrecta. Introduzca al menos 8 caracteres con una mayúscula.</span>
          </div>
          <div class="form-group position-relative">
            <label for="confirmPassword">Confirmar Contraseña:</label>
            <input
                :type="showConfirmPassword? 'text' : 'password'"
                class="form-control"
                id="confirmPassword"
                v-model="confirmPassword"
                @change="contrasenaCoincide"
                required
            >
            <button
                type="button"
                class="btn btn-secondary position-absolute end-0 top-50 translate-middle-y show-password-btn"
                style="width: 40px; height: 40px; padding: 0;"
                @click="showConfirmPassword =!showConfirmPassword"
            >
              <i :class="showConfirmPassword? 'fa fa-eye-slash' : 'fa fa-eye-slash'" aria-hidden="true"></i>
            </button>
            <span v-if="!passwordsCoinciden" class="text-danger">Las contraseñas no coinciden.</span>
          </div>
        </div>
        <div class="col-md-4">
          <div class="form-group d-flex flex-column align-items-start">
            <label for="fotoPerfil">Foto de Perfil:</label>
            <div v-if="imagePreview" class="image-preview">
              <img :src="imagePreview" alt="Previsualización de la imagen" class="rounded-circle">
            </div>
            <div v-if="showFormatWarning" class="text-danger">Por favor, seleccione una imagen en formato JPG o JPEG.</div>
            <input type="file" class="form-control-file" id="fotoPerfilInput" style="display: none;" @change="onFileChange">
            <button type="button" class="btn btn-primary" id="seleccionarFotoPerfil">Seleccionar Foto</button>
          </div>

          <!-- Botón de Agregar Usuario -->
          <div class="form-group form-check">
            <input type="checkbox" class="form-check-input" id="aceptarCondiciones" required>
            <label class="form-check-label" for="aceptarCondiciones">
              Acepto las <a href="#" @click="mostrarCondiciones()">condiciones de servicio y términos</a>
            </label>
          </div>

          <div class="form-group">
            <button type="submit" class="btn btn-primary" :disabled="!aceptarCondiciones">Agregar Usuario</button>
          </div>
        </div>
      </form>
      <div v-if="errorMessage" class="alert alert-danger mt-3">{{ errorMessage }}</div>
    </div>
  </div>
  <div class="extra-space">
    <footer-componente/>
  </div>
</template>

<script>
import UsuarioServicio from "../services/UsuarioServicio";
import CabeceraComponente2 from "@/components/header2.vue";
import FooterComponente from "@/components/footer.vue";

export default {
  name: 'AgregarUsuario',
  components: {FooterComponente, CabeceraComponente2},
  data() {
    return {
      nuevoUsuario: {
        dni: '',
        username: '',
        nombre: '',
        apellidos: '',
        fechaNacimiento: '',
        telefono: '',
        fotoPerfil: null,
        email: '',
        password: ''
      },
      confirmPassword: '',
      imagePreview: '',
      showFormatWarning: false,
      showPassword: false,
      showConfirmPassword: false,
      dniValido: false,
      emailValido: false,
      telefonoValido: false,
      passwordValida: false,
      passwordsCoinciden: false,
      mayorDeEdad: false,
      errorMessage: '',
      aceptarCondiciones: false
    }
  },
  mounted() {
    document.getElementById('seleccionarFotoPerfil').addEventListener('click', () => {
      document.getElementById('fotoPerfilInput').click();
    });
  },
  methods: {
    agregarUsuario() {
      // Validaciones
      if (!this.dniValido) {
        alert('DNI no válido');
        return;
      }
      if (!this.emailValido) {
        alert('Email no válido');
        return;
      }
      if (!this.telefonoValido) {
        alert('Teléfono no válido');
        return;
      }
      if (!this.passwordValida) {
        alert('La contraseña no cumple con los requisitos');
        return;
      }
      if (!this.passwordsCoinciden) {
        alert('Las contraseñas no coinciden');
        return;
      }
      if (!this.mayorDeEdad) {
        alert('Debes ser mayor de edad para registrarte');
        return;
      }

      const formData = new FormData();
      formData.append('dni', this.nuevoUsuario.dni);
      formData.append('username', this.nuevoUsuario.username);
      formData.append('nombre', this.nuevoUsuario.nombre);
      formData.append('apellidos', this.nuevoUsuario.apellidos);
      formData.append('fechaNacimiento', this.nuevoUsuario.fechaNacimiento);
      formData.append('telefono', this.nuevoUsuario.telefono);
      formData.append('fotoPerfil', this.nuevoUsuario.fotoPerfil);
      formData.append('email', this.nuevoUsuario.email);
      formData.append('password', this.nuevoUsuario.password);

      UsuarioServicio.agregarUsuario(formData)
          .then(() => {
            this.nuevoUsuario = {
              dni: '',
              username: '',
              nombre: '',
              apellidos: '',
              fechaNacimiento: '',
              telefono: '',
              fotoPerfil: null,
              email: '',
              password: ''
            };
            this.confirmPassword = '';
            this.errorMessage = '';
            this.$emit('usuario-agregado');
            this.$router.push('/iniciaSesion');
          })
          .catch(error => {
            console.error('Error al agregar usuario:', error);
            if (error.response && error.response.data && error.response.data.message) {
              this.errorMessage = error.response.data.message;
            } else {
              this.errorMessage = 'Error al agregar el usuario. Inténtelo de nuevo más tarde.';
            }
          });
    },
    validarEmail() {
      this.emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.nuevoUsuario.email);
    },
    validarDni() {
      this.dniValido = /\d{8}[A-HJ-NP-TV-Z]/.test(this.nuevoUsuario.dni);
    },
    validarTelefono() {
      if (this.nuevoUsuario.telefono) {
        const telefonoValido = /^\d{9}$/.test(this.nuevoUsuario.telefono);
        const telefonoConAnnotacionValido = /^\d{9}$/.test(this.nuevoUsuario.telefono);
        this.telefonoValido = telefonoValido && telefonoConAnnotacionValido;
      } else {
        this.telefonoValido = false;
      }
    },
    validarPassword() {
      if (this.nuevoUsuario.password) {
        this.passwordValida = /^(?=.*[A-Z]).{8,}$/.test(this.nuevoUsuario.password);
      } else {
        this.passwordValida = false;
      }
    },
    contrasenaCoincide() {
      this.passwordsCoinciden = this.nuevoUsuario.password === this.confirmPassword;
    },
    validarEdad() {
      const fechaNacimiento = new Date(this.nuevoUsuario.fechaNacimiento);
      const edadMinima = 18;

      const hoy = new Date();
      let edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
      const mes = hoy.getMonth() - fechaNacimiento.getMonth();

      if (mes < 0 || (mes === 0 && hoy.getDate() < fechaNacimiento.getDate())) {
        edad = edad - 1;
      }

      this.mayorDeEdad = edad >= edadMinima;
    },
    onFileChange(event) {
      const selectedFile = event.target.files[0];

      if (selectedFile) {
        if (!['image/jpeg', 'image/jpg'].includes(selectedFile.type)) {
          this.showFormatWarning = true;
          return;
        }
        this.showFormatWarning = false;

        const reader = new FileReader();

        reader.onload = (e) => {
          this.imagePreview = e.target.result;
          this.nuevoUsuario.fotoPerfil = e.target.result.split(',')[1];
        };

        reader.readAsDataURL(selectedFile);
      } else {
        this.imagePreview = '';
        this.nuevoUsuario.fotoPerfil = null;
        this.showFormatWarning = false;
      }
    },
    mostrarCondiciones() {
      alert('Por favor, lea detenidamente nuestros términos y condiciones antes de continuar. Estos términos son importantes para tu participación en nuestra comunidad celiaca. A continuación, te presentamos un resumen de nuestras políticas y compromisos:\n' +
          '\n' +
          'Ocio Sin Gluten informa que:\n' +
          'Uso del Servicio: Al registrarte en Ocio Sin Gluten, aceptas cumplir con nuestras normas comunitarias y respetar a otros miembros.\n' +
          '\n' +
          'Privacidad: Tu privacidad es importante para nosotros. Nos comprometemos a proteger tus datos personales según nuestras políticas de privacidad.\n' +
          '\n' +
          'Seguridad: Mantenemos altos estándares de seguridad para proteger tu información personal y tus interacciones en nuestra plataforma.\n' +
          '\n' +
          'Responsabilidades: Como miembro de Ocio Sin Gluten, eres responsable del contenido que compartes y de tu conducta en nuestra comunidad.\n' +
          '\n' +
          'Al hacer clic en "Aceptar", confirmas que has leído y entendido nuestros términos y condiciones completos.');
      this.aceptarCondiciones = true;  // Marcar como aceptadas las condiciones
    }
  }
}
</script>

<style scoped>
.container1 {
  background-image: url("@/assets/images/_01d90abf-9b74-4813-b728-42c7b8f918a7.jpg");
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-repeat: no-repeat;
  background-size: cover;
  padding: 20px;
}

@media screen and (max-width: 768px) {
  .container1 {
    background-image: url("@/assets/images/_01d90abf-9b74-4813-b728-42c7b8f918a7.jpg");
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-repeat: no-repeat;
    background-size: cover;
    padding: 20px;
  }

  .container2 {
    width: 90vw;
    padding: 20px;
  }

  .title {
    font-size: 24px;
    margin-bottom: 20px;
  }


  .form-group:last-child {
    text-align: center;
  }

  .text-danger {
    text-align: center;
  }

  h1 {
    font-size: 22px;
    font-weight: bold;
    color: #000000;
    text-align: center;
    margin-bottom: 30px;
  }
}

@media screen and (max-width: 576px) {
  .form-control {
    padding: 10px;
  }

  .image-preview {
    width: 150px;
    height: 150px;
  }
}

@media screen and (max-width: 576px) {
  .btn-primary {
    padding: 10px 20px;
  }

  .show-password-btn {
    padding: 10px 15px;
  }
}

@import '../assets/css/registro.css';
</style>