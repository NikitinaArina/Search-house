<template>
    <div class="d-flex flex-wrap justify-center">
        <v-form ref="form" class="w-50">
            <v-textarea clearable label="Сообщение"
                        variant="outlined"
                        v-model="this.messageDto.message"
                        :rules="[reqValidation]"></v-textarea>
            <v-btn color="success" @click="sendMessage" class="mt-2">Отправить</v-btn>
        </v-form>
    </div>
</template>

<script>

import MessageService from "@/services/MessageService";

export default {
    data() {
        return {
            messageDto: {
                message: null
            }
        }
    },
    methods: {
        async sendMessage() {
            if ((await this.$refs.form.validate()).valid) {
                MessageService.sendMessage(this.messageDto).then(res => {
                    if (res.status === 200) {
                        this.messageDto.message = null
                    }
                })
            }
        },
        reqValidation(value) {
            if (value === 0 || value === '' || value === null) {
                return 'Поле обязательное'
            }
            return true
        },
    }
}
</script>