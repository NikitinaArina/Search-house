import httpResource from "@/services/HttpResource";

const ADS = `ads/`
const SAVE = `save`
const DELETE = `delete/`

class AdService {
    async getAd(userId, adId) {
        return await httpResource.get(ADS + `${userId}` + '/' + `${adId}`)
    }

    async getAllAd(userId) {
        return await httpResource.get(ADS + `${userId}`)
    }

    async saveAd(payload, file) {
        let formData = new FormData();
        const json = JSON.stringify(payload);
        const blob = new Blob([json], {
            type: 'application/json'
        });
        if (file !== undefined) {
            formData.append('file', file, file.name);
        }
        formData.append("ad", blob);

        return await httpResource.post(ADS + SAVE, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }

    async deleteAd(adId) {
        return await httpResource.delete(ADS + DELETE + `${adId}`).then(res => {
            console.log(res.status)
        });
    }
}

export default new AdService();