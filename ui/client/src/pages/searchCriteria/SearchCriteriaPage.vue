<template>
    <v-app>
        <div class="d-flex flex-wrap justify-center">
            <h4 class="mt-2">Мои настройки поиска</h4>
        </div>
        <div class="d-flex flex-wrap justify-center">
            <v-btn v-if="this.searchCriterias.length === 0"
                   @click="this.$router.push({name: 'search-criteria-save', query: {userId: this.getUserId}})">
                Добавить настройку поиска
            </v-btn>
            <v-card class="mb-2 mr-2 mt-2 ml-2" width="350"
                    v-for="searchCriteria in this.searchCriterias"
                    :key="searchCriteria.id">
                <v-card-title>{{ searchCriteria.name }}</v-card-title>
                <v-card-subtitle>{{ searchCriteria.location.map(m => m.location).join(',') }}</v-card-subtitle>
                <v-card-text>
                    <p> Город: {{ searchCriteria.city }} </p>
                    <p v-if="searchCriteria.price.from !== null && searchCriteria.price.to !== null"> Цена от:
                        {{ searchCriteria.price.from }} до {{ searchCriteria.price.to }} </p>
                    <p v-else-if="searchCriteria.price.from !== null"> Цена от: {{ searchCriteria.price.from }} </p>
                    <p v-else-if="searchCriteria.price.to !== null"> Цена до: {{ searchCriteria.price.to }} </p>
                </v-card-text>
                <v-card-actions>
                    <v-btn text color="primary"
                           @click="this.$router.push({name: 'search-criteria-save-el',
                           params: {userId: this.getUserId, criteriaId: searchCriteria.id}})">Редактировать
                    </v-btn>
                    <v-btn text @click="deleteSearchCriteria(searchCriteria.id)">Удалить</v-btn>
                </v-card-actions>
            </v-card>
        </div>
    </v-app>
</template>

<script>
import {mapGetters} from "vuex";
import SearchCriteriaService from "@/services/SearchCriteriaService";

export default {
    data() {
        return {
            tab: null,
            searchCriterias: []
        }
    },
    async created() {
        await this.getAllSearchCriteria(this.getUserId)
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
            getUserId: "getUserId",
        }),
    },
    mounted() {
        this.getAllSearchCriteria(this.getUserId)
    },
    methods: {
        async getAllSearchCriteria(userId) {
            await SearchCriteriaService.getAllSearchCriterias(userId).then(res => {
                if (Object.values(res.data).some(x => x !== null && x !== '')) {
                    this.searchCriterias = res.data
                }
            })
        },
        async deleteSearchCriteria(criteriaId) {
            await SearchCriteriaService.deleteSearchCriteria(criteriaId).then(() => {
                this.getAllSearchCriteria(this.getUserId)
            })
        },
    }
}

</script>