import httpResource from "@/services/HttpResource";

const AUTH = `api/auth/`

class AuthorizationService {
    async authenticateUser(payload) {
        return await httpResource.post(AUTH + "signin", payload)
            .then(res => {
                return res
            });
    }
}

/*class LoginRequest {
    username;
    password;
    constructor(username, password) {
        this.username = username;
        this.password = password;
    }
}*/

/*class JwtResponse {
    token;
    id;
    username;
    roles;
    constructor(data) {
        this.token = data.token;
        this.id = data.id;
        this.username = data.username;
        this.roles = data.roles;
    }
}*/

export default new AuthorizationService();