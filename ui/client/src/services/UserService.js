import httpResource from "@/services/HttpResource";

const USER = `user/`
const UPDATE = `update`

class UserService {
    async getAll() {
        return await httpResource.get(USER)
    }

    async updateUser(usr) {
        return await httpResource.patch(USER + UPDATE, usr)
    }
}

export default new UserService();