import AuthorizationService from "@/services/AuthorizationService";

const state = () => ({
    loginApiStatus: "",
    userId: "",
    roles: [],
});

const getters = {
    getLoginApiStatus(state) {
        return state.loginApiStatus;
    },
    getUserId(state) {
        return state.userId;
    },
    getRoles(state) {
        return state.roles;
    }
};

const actions = {
    async loginApi({commit}, payload) {
        const response = await AuthorizationService.authenticateUser(payload)

        if (response && response.data) {
            commit("setLoginApiStatus", "success");
            commit("setUserId", response.data.id);
            commit("setRoles", response.data.roles);
        } else {
            commit("setLoginApiStatus", "failed");
        }
    },
    async updateUserInfo({commit}, roles) {
        commit("setRoles", roles);
    }
};

const mutations = {
    setLoginApiStatus(state, data) {
        state.loginApiStatus = data;
    },
    setUserId(state, data) {
        state.userId = data;
    },
    setRoles(state, data) {
        state.roles = data;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations,
};
