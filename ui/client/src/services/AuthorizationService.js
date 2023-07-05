import httpResource from "@/services/HttpResource";

const AUTH = `api/auth/`

class AuthorizationService {
    async authenticateUser(payload) {
        return await httpResource.post(AUTH + "signin", payload)
            .then(res => {
                return res
            });
    }

    async getUserInfo(userId) {
        return await httpResource.get(AUTH + "info/" + `${userId}`)
            .then(res => {
                return res
            });
    }
}

export default new AuthorizationService();