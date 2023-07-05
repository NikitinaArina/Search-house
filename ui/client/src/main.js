import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/index'
import SockJS from 'sockjs-client'
import Stomp from "webstomp-client";

import 'vuetify/styles'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/dist/vuetify.min.css'

const vuetify = createVuetify({
    components,
    directives,
})
const vueApp = createApp(App);
vueApp.config.productionTip = false
vueApp.use(vuetify)
    .use(router)
    .use(store)
    .mount('#app');

router.beforeEach(async (to, from, next) => {
    if (store.getters["auth/getLoginApiStatus"] !== 'success') {
        if (to.query.user_id !== undefined) {
            let query = to.query.user_id.split(/_{2}[a-z]|-{2}[a-z]/)

            const payload = {
                username: atob(query[2]),
                password: atob(query[1]) + atob(query[2]),
            }

            await store.dispatch("auth/loginApi", payload)
            await establishWebSocketConnection()

            to.query.user_id = store.getters["auth/getUserId"]
        } else if (to.params.includes('userId')) {
            to.params.userId = store.getters["auth/getUserId"]
        }

        next(to)
    } else {
        await establishWebSocketConnection()
        next()
    }
})

function establishWebSocketConnection() {
    const socket = new SockJS('http://127.0.0.1:8088/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/role-updates', async function (val) {
            console.log(val);
            await store.dispatch("auth/updateUserInfo", (val.body));
        });
    });
}