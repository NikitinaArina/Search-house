<template>
    <v-app>
        <v-tabs
                v-model="tab"
                color="deep-purple-accent-4"
                align-tabs="center"
        >
            <v-tab value="one">Пользователи</v-tab>
            <v-tab value="two">Отправить сообщение</v-tab>
            <v-tab value="three">Графики</v-tab>
        </v-tabs>
        <v-card-text>
            <v-window v-model="tab">
                <v-window-item value="one" class="mt-2">
                    <users-list/>
                </v-window-item>

                <v-window-item value="two" class="mt-2">
                    <send-message/>
                </v-window-item>

                <v-window-item value="three" class="mt-2">
                    <a href="http://127.0.0.1:3000/d/p3o5eouVz/poisk-zhil-ia?from=1687361201579&to=1687362388576">Открыть сервис со статистикой и графиками</a>
                </v-window-item>
            </v-window>
        </v-card-text>
    </v-app>
</template>

<script>
import UsersList from "@/components/admin/UsersList.vue"
import SendMessage from "@/components/admin/SendMessage.vue"

export default {
    components: {UsersList, SendMessage},
    data() {
        return {
            tab: null,
        }
    }
}
</script>