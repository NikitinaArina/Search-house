<template>
    <v-container class="d-flex justify-content-center">
        <v-form class="w-50">
            <v-row>
                <v-col>
                    <v-select
                            v-model="ad.owner"
                            :items="ownerOptions"
                            label="Владелец"
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="ad.renovation"
                            :items="renovationOptions"
                            label="Ремонт"
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="ad.rooms"
                            :items="roomsOptions"
                            label="Количество комнат"
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-text-field
                            type="number"
                            v-model="ad.year"
                            label="Год"
                            outlined
                            :rules="[yearValidation]"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-text-field
                            type="number"
                            v-model="ad.price"
                            label="Цена"
                            outlined
                            :rules="[priceValidation]"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-text-field
                            type="number"
                            v-model="ad.floor"
                            label="Этаж"
                            outlined
                            :rules="[floorValidation]"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-text-field
                            v-model="ad.location"
                            label="Адрес"
                            outlined></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-checkbox
                            v-model="ad.isChildren"
                            label="Можно с детьми"
                    ></v-checkbox>
                    <v-checkbox
                            v-model="ad.isAnimal"
                            label="Можно с животными"
                    ></v-checkbox>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-file-input
                            show-size
                            label="Выберете изображение"
                            accept="image/*"
                            @change="selectImage"
                            @click:clear="cancelImage"
                    ></v-file-input>
                </v-col>
            </v-row>
            <v-row v-if="previewImage">
                <v-col>
                    <v-img class="preview my-3" :src="previewImage" alt=""/>
                </v-col>
            </v-row>
            <v-row>
                <v-col class="d-flex justify-content-center">
                    <v-btn color="success" @click="submitForm">Сохранить</v-btn>
                </v-col>
            </v-row>
        </v-form>
    </v-container>
</template>

<script>
import AdService from "@/services/AdService";
import {mapActions, mapGetters} from "vuex";

export default {
    data() {
        const currentYear = new Date().getFullYear();

        let userId = null

        return {
            currentImage: undefined,
            previewImage: undefined,
            ad: {
                userId: null,
                rooms: [],
                owner: [],
                renovation: [],
                price: null,
                floor: null,
                year: null,
                location: '',
                isChildren: false,
                isAnimal: false,
            },
            ownerOptions: ['Собственник', 'Риелтор'],
            renovationOptions: ['Косметический', 'Евроремонт', 'Дизайнерский', 'Без ремонта'],
            roomsOptions: ['Студия', '1', '2', '3', '4', '5+'],
            currentYear,
            userId
        };
    },
    async created() {
        let query = this.$route.query.user_id.split(/_{2}[a-z]|-{2}[a-z]/)

        this.userId = query[0]

        const payload = {
            username: atob(query[2]),
            password: atob(query[1]) + atob(query[2]),
        };

        await this.login(this.userId, payload)

        await this.getAd(this.userId)
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
        }),
    },
    async mounted() {
        await this.getAd(this.userId);
    },
    methods: {
        ...mapActions("auth", {
            actionLoginApi: "loginApi",
        }),
        async login(userId, payload) {
            await this.actionLoginApi(payload);
            if (this.getLoginApiStatus === "success") {
                this.$router.push({name: 'ad-save', query: {user_id: this.userId}})
            } else {
                alert("failed")
            }
        },
        selectImage(image) {
            this.currentImage = image.target.files[0]
            this.previewImage = URL.createObjectURL(this.currentImage)
        },
        cancelImage() {
            this.previewImage = undefined
        },
        async getAd(userId) {
            await AdService.getAd(userId).then(res => {
                if (Object.values(res.data).some(x => x !== null && x !== '')) {
                    this.ad = res.data
                }
            })
        },
        yearValidation(value) {
            if (value === '' || value === null) {
                return false
            }
            if (value < 0) {
                return 'Значение должно быть положительным';
            }
            if (value < 1800) {
                return 'Минимальное значение 1800'
            }
            if (value > this.currentYear) {
                return 'Максимальное значение ' + this.currentYear
            }
        },
        priceValidation(value) {
            if (value === '' || value === null) {
                return false
            }
            if (value < 0) {
                return 'Значение должно быть положительным'
            }
        },
        floorValidation(value) {
            if (value === '' || value === null) {
                return false
            }
            if (value < 0) {
                return 'Значение должно быть положительным'
            }
        },
        async submitForm() {
            this.ad.userId = this.userId
            await AdService.saveAd(this.ad, this.currentImage)
            await this.getAd(this.userId)
        },
    },
}
</script>