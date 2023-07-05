<template>
    <v-app>
        <div class="d-flex flex-wrap justify-center">
            <h4 class="mt-2">Мои объявления</h4>
        </div>
        <div class="d-flex flex-wrap justify-center">
            <v-btn v-if="this.ads.length === 0"
                   @click="this.$router.push({name: 'ad-save', query: {userId: this.getUserId}})">
                Добавить объявление
            </v-btn>
            <v-card class="mb-2 mr-2 mt-2" width="350" v-for="ad in this.ads" :key="ad.id">
                <v-card-title>{{ ad.title }}</v-card-title>
                <v-card-subtitle>{{ ad.location }}</v-card-subtitle>
                <v-card-text>
                    <p> Город: {{ ad.city }} </p>
                    <p> Цена: {{ ad.price }} </p>
                </v-card-text>
                <v-card-actions>
                    <v-btn text color="primary"
                           @click="this.$router.push({name: 'ad-save-el',
                           params: {userId: this.getUserId, adId: ad.id}})">Редактировать
                    </v-btn>
                    <v-btn text @click="deleteAd(ad.id)">Удалить</v-btn>
                </v-card-actions>
            </v-card>
        </div>
    </v-app>
</template>

<script>
import AdService from "@/services/AdService";
import {mapGetters} from "vuex";

export default {
    data() {
        return {
            ads: []
        }
    },
    async mounted() {
        this.$router.push({name: 'ads', params: {userId: this.getUserId}})
        await this.getAds()
    },
    methods: {
        async getAds() {
            AdService.getAllAd(this.getUserId).then(res => {
                this.ads = res.data;
            })
        },
        async deleteAd(adId) {
            await AdService.deleteAd(adId).then(() => {
                this.getAds()
            })
        },
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
            getUserId: "getUserId",
        }),
    },
}

</script>