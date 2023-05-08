<template>
    <v-container class="d-flex justify-content-center">
        <v-form class="w-50">
            <v-row>
                <v-col>
                    <v-select
                            v-model="searchCriteria.owner"
                            :items="ownerOptions"
                            label="Владелец"
                            multiple
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="searchCriteria.renovation"
                            :items="renovationOptions"
                            label="Ремонт"
                            multiple
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="searchCriteria.rooms"
                            :items="roomsOptions"
                            label="Количество комнат"
                            multiple
                            small-chips
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-row>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.year.from"
                                    label="Год (от)"
                                    outlined
                                    :rules="[yearValidation]"
                            ></v-text-field>
                        </v-col>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.year.to"
                                    label="Год (до)"
                                    outlined
                                    :rules="[yearValidation]"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-row>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.price.from"
                                    label="Цена (от)"
                                    outlined
                                    :rules="[priceValidation]"
                            ></v-text-field>
                        </v-col>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.price.to"
                                    label="Цена (до)"
                                    outlined
                                    :rules="[priceValidation]"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-row>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.floor.from"
                                    label="Этаж (от)"
                                    outlined
                                    :rules="[floorValidation]"
                            ></v-text-field>
                        </v-col>
                        <v-col>
                            <v-text-field
                                    type="number"
                                    v-model="searchCriteria.floor.to"
                                    label="Этаж (до)"
                                    outlined
                                    :rules="[floorValidation]"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-text-field
                            v-model="searchCriteria.location"
                            label="Адрес"
                            outlined></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-checkbox
                            v-model="searchCriteria.isChildren"
                            label="Можно с детьми"
                    ></v-checkbox>
                    <v-checkbox
                            v-model="searchCriteria.isAnimal"
                            label="Можно с животными"
                    ></v-checkbox>
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
import SearchCriteriaService from "@/services/SearchCriteriaService";
import {mapActions, mapGetters} from "vuex";

export default {
    data() {
        const currentYear = new Date().getFullYear();

        let userId = null

        return {
            searchCriteria: {
                userId: null,
                rooms: [],
                owner: [],
                renovation: [],
                price: {
                    from: null,
                    to: null
                },
                floor: {
                    from: null,
                    to: null
                },
                year: {
                    from: null,
                    to: null
                },
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

        await this.getSearchCriteria(this.userId)
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
        }),
    },
    async mounted() {
        await this.getSearchCriteria(this.userId);
    },
    methods: {
        ...mapActions("auth", {
            actionLoginApi: "loginApi",
        }),
        async login(userId, payload) {
            await this.actionLoginApi(payload);
            if (this.getLoginApiStatus === "success") {
                this.$router.push({name: 'search-criteria-save', query: {user_id: this.userId}})
            } else {
                alert("failed")
            }
        },
        async getSearchCriteria(userId) {
            await SearchCriteriaService.getSearchCriteria(userId).then(res => {
                if (Object.values(res.data).some(x => x !== null && x !== '')) {
                    this.searchCriteria = res.data
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
            if (value > this.searchCriteria.year.to && this.searchCriteria.year.to !== null) {
                return 'Минимальное значение не может быть больше максимального'
            }
            if (this.searchCriteria.year.from > value && this.searchCriteria.year.from !== null) {
                return 'Максимальное значение не может быть меньше минимального'
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
            if (value > this.searchCriteria.price.to && this.searchCriteria.price.to !== null) {
                return 'Минимальное значение не может быть больше максимального'
            }
            if (this.searchCriteria.price.from > value && this.searchCriteria.price.from !== null) {
                return 'Максимальное значение не может быть меньше минимального'
            }
        },
        floorValidation(value) {
            if (value === '' || value === null) {
                return false
            }
            if (value < 0) {
                return 'Значение должно быть положительным'
            }
            if (value > this.searchCriteria.floor.to && this.searchCriteria.floor.to !== null) {
                return 'Минимальное значение не может быть больше максимального'
            }
            if (this.searchCriteria.floor.from > value && this.searchCriteria.floor.from !== null) {
                return 'Максимальное значение не может быть меньше минимального'
            }
        },
        async submitForm() {
            this.searchCriteria.userId = this.userId
            await SearchCriteriaService.saveSearchCriteria(this.searchCriteria)
            await this.getSearchCriteria(this.userId)
        },
    },
}
</script>