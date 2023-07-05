import {createStore} from "vuex";
import authModule from './modules/auth';
import createPersistedState from "vuex-persistedstate";

const store = createStore({
    plugins: [
        createPersistedState({
            storage: window.sessionStorage,
        }),
    ],
    modules: {
        auth: authModule,
    }
});

export default store;