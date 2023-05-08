<template>
  <v-app class="d-flex flex-column">
    <NavbarMain/>
    <v-main>
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>
import NavbarMain from './components/NavbarMain.vue'

export default {
  name: 'App',
  components: {
    NavbarMain,
  }
}
</script>
