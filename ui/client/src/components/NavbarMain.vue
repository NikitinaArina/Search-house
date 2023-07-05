<template>
    <div>
        <v-app-bar class="d-flex justify-space-between" :elevation="2">
            <div class="v-toolbar__content d-flex justify-lg-space-between">
                <div class="v-toolbar-title v-app-bar-title">
                    <div class="v-toolbar-title__placeholder" @click="this.$router.push({name: 'search-criteria',
        params: {userId: this.getUserId}})" style="cursor: pointer;">Поиск жилья
                    </div>
                </div>
                <v-menu>
                    <template v-slot:activator="{ props }">
                        <v-btn icon="mdi-dots-vertical" v-bind="props"></v-btn>
                    </template>

                    <v-list>
                        <v-list-item v-for="(item, i) in filteredItems" :key="i"
                                     @click="() => {openPage(item.title)}">
                            {{ item.title }}
                        </v-list-item>
                    </v-list>
                </v-menu>
            </div>
        </v-app-bar>
    </div>
</template>

<script>

import {mapGetters} from "vuex";

export default {
    data: () => ({
        items: [
            {title: 'Настройки поиска'},
            {title: 'Добавить настройку поиска'},
            {title: 'Объявления'},
            {title: 'Пользователи'}
        ],
    }),
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
            getUserId: "getUserId",
            getRoles: "getRoles"
        }),
        filteredItems() {
            return this.items.filter(item => {
                if (item.title === 'Объявления' && this.getRoles.includes('LANDLORD')) {
                    return true;
                } else if (item.title === 'Пользователи' && this.getRoles.includes('ADMIN')) {
                    return true;
                } else return item.title !== 'Объявления' && item.title !== 'Пользователи';
            });
        }
    },
    methods: {
        async openPage(title) {
            if (title === 'Настройки поиска') {
                this.$router.push({name: 'search-criteria', params: {userId: this.getUserId}})
            }
            if (title === 'Добавить настройку поиска') {
                this.$router.push({name: 'search-criteria-save', query: {userId: this.getUserId}})
            }
            if (title === 'Объявления') {
                this.$router.push({name: 'ads', params: {userId: this.getUserId}})
            }
            if (title === 'Пользователи') {
                this.$router.push({name: 'admin-page'})
            }
        }
    }
}
</script>

