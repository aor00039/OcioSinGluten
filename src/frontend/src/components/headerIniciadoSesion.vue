<script>
export default {
  name: 'header-3',
  data() {
    return {
      username: ''
    };
  },
  mounted() {
    this.username = localStorage.getItem('username');
  }
}
</script>

<template>
  <div>
    <header>
      <div class="logo-container">
        <img src="../assets/images/_8c971745-c451-48f5-8bf7-05017ae6975e.jpeg" alt="Logo de la aplicación" class="logo">
        <div class="title-container">
          <h1 class="title">Ocio Sin Gluten</h1>
          <h2 class="subtitle">Disfrute de su tiempo de ocio sin gluten</h2> <!-- Subtítulo debajo del título -->
        </div>
      </div>
      <nav>
        <ul class="main-links">
          <li><router-link to="/iniciadoSesion">Inicio</router-link></li>
          <li><a href="#">Establecimientos sin gluten</a></li>
          <li><router-link :to="{ path: '/contactoIniciadoSesion' }">Contacto</router-link></li>
          <li v-if="username">
            <span class="welcome-message">Bienvenido
              <router-link :to="'/perfil/' + username" class="welcome-link">{{ username }}</router-link>
            </span>
          </li>
        </ul>
      </nav>
    </header>
  </div>
</template>


<style scoped>
@import "../assets/css/header3.css";
</style>