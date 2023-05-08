import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/index'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/dist/vuetify.min.css'

const vuetify = createVuetify({
    components,
    directives,
})

let vueApp = createApp(App);
vueApp.config.productionTip = false
vueApp.use(vuetify)
    .use(router)
    .use(store)
    .mount('#app');