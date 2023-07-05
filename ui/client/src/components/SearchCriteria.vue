<template>
    <v-container class="d-flex justify-content-center">
        <v-form ref="form" class="w-50">
            <v-row>
                <v-col>
                    <v-text-field
                            v-model="searchCriteria.name"
                            label="Название"
                            :rules="[reqValidation]"
                            outlined></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="searchCriteria.city"
                            :items="cityOptions"
                            label="Город"
                            small-chips
                            :rules="[selectValidation]"
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
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
                    <v-autocomplete
                            v-model="searchCriteria.location"
                            :items="locationOptions"
                            item-text="Адрес"
                            item-value="Адреса"
                            placeholder="Выберите районы"
                            multiple
                    ></v-autocomplete>
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
import {mapGetters} from "vuex";
import axios from "axios";

export default {
    data() {
        const currentYear = new Date().getFullYear();

        const options = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Authorization': 'Token ' + 'e37be6cb21615561f9b941d89e43503176088168'
            }
        };

        return {
            searchCriteria: {
                userId: null,
                name: '',
                city: '',
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
                location: [],
                isChildren: false,
                isAnimal: false,
            },
            locationOptions: [],
            cityOptions: ['Саратов', 'Энгельс'],
            ownerOptions: ['Собственник', 'Риелтор'],
            renovationOptions: ['Косметический', 'Евроремонт', 'Дизайнерский', 'Без ремонта'],
            roomsOptions: ['Студия', '1', '2', '3', '4', '5+'],
            currentYear,
            options
        };
    },
    async created() {
        if (this.$route.params.criteriaId !== undefined) {
            await this.getSearchCriteria(this.getUserId, this.$route.params.criteriaId)
        }
    },
    watch: {
        'searchCriteria.city'(newCity, oldCity) {
            if (newCity !== oldCity) {
                this.getDataFromServer();
            }
        },
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
            getUserId: "getUserId",
        }),
    },
    async mounted() {
        if (this.$route.params.criteriaId !== undefined) {
            await this.getSearchCriteria(this.getUserId, this.$route.params.criteriaId);
        }
    },
    methods: {
        async getSearchCriteria(userId, criteriaId) {
            await SearchCriteriaService.getSearchCriteria(userId, criteriaId).then(res => {
                if (Object.values(res.data).some(x => x !== null && x !== '')) {
                    this.searchCriteria = res.data
                    this.searchCriteria.location = this.searchCriteria.location.map(m => m.location)
                }
            })
        },
        yearValidation(value) {
            if (value !== "" && value !== null) {
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
            }
            return true
        },
        priceValidation(value) {
            if (value !== "" && value !== null) {
                if (value < 0) {
                    return 'Значение должно быть положительным'
                }
                if (value > this.searchCriteria.price.to && this.searchCriteria.price.to !== null) {
                    return 'Минимальное значение не может быть больше максимального'
                }
                if (this.searchCriteria.price.from > value && this.searchCriteria.price.from !== null) {
                    return 'Максимальное значение не может быть меньше минимального'
                }
            }
            return true
        },
        floorValidation(value) {
            if (value !== "" && value !== null) {
                if (value < 0) {
                    return 'Значение должно быть положительным'
                }
                if (value > this.searchCriteria.floor.to && this.searchCriteria.floor.to !== null) {
                    return 'Минимальное значение не может быть больше максимального'
                }
                if (this.searchCriteria.floor.from > value && this.searchCriteria.floor.from !== null) {
                    return 'Максимальное значение не может быть меньше минимального'
                }
            }
            return true
        },
        selectValidation(value) {
            if (value.length === 0) {
                return 'Поле обязательное'
            }
            return true
        },
        reqValidation(value) {
            if (value === 0 || value === '' || value === null) {
                return 'Поле обязательное'
            }
            return true
        },
        async submitForm() {
            if ((await this.$refs.form.validate()).valid) {
                this.searchCriteria.userId = this.getUserId
                await SearchCriteriaService.saveSearchCriteria(this.searchCriteria)
            }
        },
        getDataFromServer() {
            axios
                .post('https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address', {
                    query: this.searchCriteria.city + ' р-н',
                    count: 20
                }, this.options)
                .then(response => {
                    this.locationOptions = Array.from(new Set(response.data.suggestions.map(m => m.unrestricted_value).filter(f => f.includes('р-н'))
                        .map(m => m.match(/,\s[а-яА-Я]+\sр-н,*|,\sр-н\s[а-яА-Я]+,*|,\s[а-яА-Я]+\s[а-яА-Я]+\sр-н/gm))
                        .flatMap(m => m)
                        .map(m => m.split(' '))
                        .flatMap(m => m)
                        .filter(f2 => !f2.includes(',') && !f2.includes('р-н'))));
                })
                .catch(error => {
                    console.error(error);
                })
        }
    },
}
</script>