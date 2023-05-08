import { createRouter, createWebHistory } from 'vue-router'

import SearchCriteriaPage from "@/pages/SearchCriteriaPage.vue";
import AdFormPage from "@/pages/AdFormPage.vue";

const routes = [
    { path: '/search-criteria/save',
        component: SearchCriteriaPage,
        name: 'search-criteria-save',
        props: (route) => ({ userId: route.query.user_id })},
    { path: '/ad/save',
        component: AdFormPage,
        name: 'ad-save',
        props: (route) => ({ userId: route.query.user_id })},
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router