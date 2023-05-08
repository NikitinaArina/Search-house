import AuthorizationService from "@/services/AuthorizationService";

const state = () => ({
    loginApiStatus: "",
});

const getters = {
    getLoginApiStatus(state) {
        return state.loginApiStatus;
    },
};

const actions = {
    async loginApi({ commit }, payload) {
        const response = await AuthorizationService.authenticateUser(payload)

        if (response && response.data) {
            commit("setLoginApiStatus", "success");
        } else {
            commit("setLoginApiStatus", "failed");
        }
    },
};

const mutations = {
    setLoginApiStatus(state, data) {
        state.loginApiStatus = data;
    },
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations,
};