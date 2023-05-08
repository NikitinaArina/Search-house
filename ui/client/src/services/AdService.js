import httpResource from "@/services/HttpResource";

const ADS = `ads/`
const SAVE = `save`

class AdService {
    async getAd(userId) {
        return await httpResource.get(ADS + `${userId}`)
    }

    async saveAd(payload, file) {
        let formData = new FormData();
        const json = JSON.stringify(payload);
        const blob = new Blob([json], {
            type: 'application/json'
        });
        formData.append('file', file, file.name);
        formData.append("ad", blob);

        return await httpResource.post(ADS + SAVE, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
}

export default new AdService();