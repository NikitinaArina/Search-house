import httpResource from "@/services/HttpResource";

const CRITERIA = `criteria/`
const SAVE = `save`

class SearchCriteriaService {
    async getSearchCriteria(userId) {
        return await httpResource.get(CRITERIA + `${userId}`)
    }

    async saveSearchCriteria(payload) {
        return await httpResource.post(CRITERIA + SAVE, payload).then(res => {
            console.log(res.data)
        });
    }
}

export default new SearchCriteriaService();