<template>
    <v-container class="d-flex justify-content-center">
        <v-form ref="form" class="w-50" lazy-validation>
            <v-row>
                <v-col>
                    <v-text-field
                            v-model="ad.title"
                            label="Название"
                            :rules="[reqValidation]"
                            outlined></v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="ad.city"
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
                            v-model="ad.owner"
                            :items="ownerOptions"
                            label="Владелец"
                            small-chips
                            :rules="[selectValidation]"
                            outlined
                    ></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col>
                    <v-select
                            v-model="ad.renovationType"
                            :items="renovationOptions"
                            label="Ремонт"
                            small-chips
                            :rules="[selectValidation]"
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
                            :rules="[selectValidation]"
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
                    <v-autocomplete
                            v-model:search="searchQuery"
                            :items="addressOptions"
                            :loading="isLoading"
                            item-text="Адрес"
                            item-value="Адреса"
                            placeholder="Введите адрес"
                            :rules="[reqValidation]"
                            @change="saveAddress"
                    ></v-autocomplete>
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
                            :rules="[selectValidation]"
                            required
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
import {mapGetters} from "vuex";
import _ from "lodash";
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
            currentImage: undefined,
            previewImage: undefined,
            searchQuery: '',
            addressOptions: [],
            selectedAddress: '',
            isLoading: false,
            debounceTimeout: null,
            ad: {
                user: {
                    id: null
                },
                city: [],
                title: null,
                rooms: [],
                owner: [],
                renovationType: [],
                price: null,
                floor: null,
                year: null,
                location: '',
                isChildren: false,
                isAnimal: false,
                filename: null,
            },
            token: "e37be6cb21615561f9b941d89e43503176088168",
            cityOptions: ['Саратов', 'Энгельс'],
            ownerOptions: ['Собственник', 'Риелтор'],
            renovationOptions: ['Косметический', 'Евроремонт', 'Дизайнерский', 'Без ремонта'],
            roomsOptions: ['Студия', '1', '2', '3', '4', '5+'],
            currentYear,
            options
        };
    },
    computed: {
        ...mapGetters("auth", {
            getLoginApiStatus: "getLoginApiStatus",
            getUserId: "getUserId",
        }),
    },
    async mounted() {
        if (this.$route.params.adId !== undefined) {
            await this.getAd(this.getUserId, this.$route.params.adId)
        }
    },
    watch: {
        searchQuery(newQuery) {
            this.debouncedSearch(newQuery);
        },
    },
    methods: {
        saveAddress(value) {
            this.ad.location = value.target._value
        },
        selectImage(image) {
            this.currentImage = image.target.files[0]
            this.previewImage = URL.createObjectURL(this.currentImage)
        },
        cancelImage() {
            this.previewImage = undefined
        },
        async getAd(userId, adId) {
            await AdService.getAd(userId, adId).then(res => {
                if (Object.values(res.data).some(x => x !== null && x !== '')) {
                    this.ad = res.data
                }
            })
        },
        yearValidation(value) {
            if (value !== "" && value !== null) {
                if (value < 0) {
                    return 'Значение должно быть положительным';
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
            if (value === 0 || value === '' || value === null) {
                return 'Поле обязательное'
            }
            if (value < 0) {
                return 'Значение должно быть положительным'
            }
        },
        floorValidation(value) {
            if (value === 0 || value === '' || value === null) {
                return 'Поле обязательное'
            }
            if (value < 0) {
                return 'Значение должно быть положительным'
            }
        },
        reqValidation(value) {
            if (value === 0 || value === '' || value === null) {
                return 'Поле обязательное'
            }
            return true
        },
        selectValidation(value) {
            if (value.length === 0) {
                return 'Поле обязательное'
            }
        },
        async submitForm() {
            if ((await this.$refs.form.validate()).valid) {
                this.ad.user.id = this.getUserId
                await AdService.saveAd(this.ad, this.currentImage)
            }
        },
        debouncedSearch: _.debounce(function (newQuery) {
            if (newQuery.length >= 3) {
                this.fetchAddressOptions();
            } else {
                this.addressOptions = [];
            }
        }, 600),
        fetchAddressOptions() {
            this.isLoading = true;
            axios
                .post('https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address', {
                    query: this.ad.city + ' ' + this.searchQuery,
                }, this.options)
                .then(response => {
                    this.addressOptions = response.data.suggestions.map(m => m.unrestricted_value).filter(f => f.includes('р-н'));
                })
                .catch(error => {
                    console.error(error);
                })
                .finally(() => {
                    this.isLoading = false;
                });
        }
    },
}
</script>