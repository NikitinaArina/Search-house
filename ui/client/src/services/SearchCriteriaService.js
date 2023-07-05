import httpResource from "@/services/HttpResource";

const CRITERIA = `criteria/`
const SAVE = `save`
const DELETE = `delete/`

class SearchCriteriaService {
    async getAllSearchCriterias(userId) {
        return await httpResource.get(CRITERIA + `${userId}`)
    }

    async getSearchCriteria(userId, criteriaId) {
        return await httpResource.get(CRITERIA + `${userId}`+ '/' + `${criteriaId}`)
    }

    async saveSearchCriteria(payload) {
        return await httpResource.post(CRITERIA + SAVE, payload).then(res => {
            console.log(res.data)
        });
    }

    async deleteSearchCriteria(criteriaId) {
        return await httpResource.delete(CRITERIA + DELETE + `${criteriaId}`).then(res => {
            console.log(res.data)
        });
    }
}

export default new SearchCriteriaService();