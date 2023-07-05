<template>
    <div class="d-flex flex-wrap justify-center">
        <v-card class="mb-2 mr-2 mt-2" width="350" v-for="usr in this.usrs" :key="usr.id">
            <v-card-title>{{ usr.username }}</v-card-title>
            <v-card-subtitle>{{ usr.chatId }}</v-card-subtitle>
            <v-card-text>
                <v-select
                        v-model="usr.roles"
                        :items="roles"
                        label="Роли"
                        small-chips
                        multiple
                        outlined
                ></v-select>
            </v-card-text>
            <v-card-actions>
                <v-btn text color="primary"
                       @click="onSubmit(usr)">Сохранить
                </v-btn>
            </v-card-actions>
        </v-card>
    </div>
</template>

<script>
import UserService from "@/services/UserService";

export default {
    data() {
        return {
            roles: ['Администратор', 'Арендодатель'],
            usrs: [],
        }
    },
    created() {
        UserService.getAll().then(async res => {
            this.usrs = res.data

            await this.translateRoles(this.usrs)
        });
    },
    methods: {
        onSubmit(usr) {
            usr.roles.push('USER')
            if (usr.roles.includes('Администратор')) {
                let index = usr.roles.indexOf('Администратор')
                if (index !== -1) {
                    usr.roles.splice(index, 1)
                    usr.roles.push('ADMIN')
                }
            }
            if (usr.roles.includes('Арендодатель')) {
                let index = usr.roles.indexOf('Арендодатель');
                if (index !== -1) {
                    usr.roles.splice(index, 1)
                    usr.roles.push('LANDLORD')
                }
            }
            UserService.updateUser(usr).then(res => {
                if (res.status === 200) {
                    UserService.getAll().then(async res => {
                        this.usrs = res.data
                        await this.translateRoles(this.usrs)
                    });
                }
            })
        },
        async translateRoles(usrs) {
            usrs.forEach(f => {
                console.log(f.roles)

                if (f.roles.includes('USER')) {
                    let index = f.roles.indexOf('USER');
                    if (index !== -1) {
                        f.roles.splice(index, 1)
                    }
                }
                if (f.roles.includes('ADMIN')) {
                    let index = f.roles.indexOf('ADMIN')
                    if (index !== -1) {
                        f.roles.splice(index, 1)
                        f.roles.push('Администратор')
                    }
                }
                if (f.roles.includes('LANDLORD')) {
                    let index = f.roles.indexOf('LANDLORD');
                    if (index !== -1) {
                        f.roles.splice(index, 1)
                        f.roles.push('Арендодатель')
                    }
                }
            })
        }
    }
}
</script>