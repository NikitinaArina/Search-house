import httpResource from "@/services/HttpResource";

const ADMIN = `admin/`
const SEND = `send/message`

class MessageService {
    async sendMessage(message) {
        return await httpResource.post(ADMIN + SEND, message)
    }
}

export default new MessageService();