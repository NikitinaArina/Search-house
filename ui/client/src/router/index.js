import {createRouter, createWebHistory} from 'vue-router'

import AdFormPage from "@/pages/ads/AdFormPage.vue";
import AdPage from "@/pages/ads/AdPage.vue";
import SearchCriteriaFormPage from "@/pages/searchCriteria/SearchCriteriaFormPage.vue";
import SearchCriteriaPage from "@/pages/searchCriteria/SearchCriteriaPage.vue";
import AdminPage from "@/pages/AdminPage.vue";

const routes = [
    {
        path: '/search-criteria/save',
        component: SearchCriteriaFormPage,
        name: 'search-criteria-save',
        props: (route) => ({userId: route.query.userId}),
    },
    {
        path: '/search-criteria/save/:userId/:criteriaId',
        component: SearchCriteriaFormPage,
        name: 'search-criteria-save-el',
    },
    {
        path: '/search-criteria/:userId',
        component: SearchCriteriaPage,
        name: 'search-criteria',
    },
    {
        path: '/ads/:userId',
        component: AdPage,
        name: 'ads'
    },
    {
        path: '/ad/save',
        component: AdFormPage,
        name: 'ad-save',
        props: (route) => ({userId: route.query.userId})
    },
    {
        path: '/ad/save/:userId/:adId',
        component: AdFormPage,
        name: 'ad-save-el'
    },
    {
        path: '/admin',
        component: AdminPage,
        name: 'admin-page'
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router