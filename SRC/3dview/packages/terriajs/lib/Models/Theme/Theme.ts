import Config from "../../../customconfig.json";
import { action, runInAction } from "mobx";

/**
 * 公開済みのデフォルトテーマ情報をviewStateにセット
 * @param viewState 
 * @returns Promise
 */
export const initSetAllThemeInformation = (viewState: any) => {
    return new Promise(function(resolve, reject) {
        fetch(Config.config.apiUrl + "/theme/all")
            .then(res => res.json())
            .then(res => {
                const themeDataList = res;
                if (themeDataList != null) {
                    runInAction(() => {
                        viewState.setThemeDataList(themeDataList);
                        viewState.setThemeData(themeDataList[0]);
                        resolve({message:"success"});
                    })
                }else{
                    reject({message:"No Data"});
                }
        }).catch(error => {
            reject({message:"error"});
            console.error('処理に失敗しました', error);
        });
    });
};

/**
 * 公開済みのテーマ一覧情報をviewStateにセット
 * @param viewState 
 * @returns Promise
 */
export const setAllThemeInformation = (viewState: any) => {
    return new Promise(function(resolve, reject) {
        fetch(Config.config.apiUrl + "/theme/all")
            .then(res => res.json())
            .then(res => {
                const themeDataList = res;
                if (themeDataList != null) {
                    runInAction(() => {
                        viewState.setThemeDataList(themeDataList);
                        resolve({message:"success"});
                    })
                }else{
                    reject({message:"No Data"});
                }
        }).catch(error => {
            reject({message:"error"});
            console.error('処理に失敗しました', error);
        });
    });
};

/**
 * 全テーマ一覧情報をviewStateにセット(管理者用)
 * @param viewState 
 * @returns Promise
 */
export const setAllThemeInformationByAdmin = (viewState: any) => {
    return new Promise(function(resolve, reject) {
        fetch(Config.config.apiUrl + "/theme/admin/all")
            .then(res => res.json())
            .then(res => {
                const themeDataList = res;
                if (themeDataList != null) {
                    runInAction(() => {
                        viewState.setThemeDataList(themeDataList);
                        resolve({message:"success"});
                    })
                }else{
                    reject({message:"No Data"});
                }
        }).catch(error => {
            reject({message:"error"});
            console.error('処理に失敗しました', error);
        });
    });
};