"use strict";

import Terria from "../Models/Terria";
import Config from "../../customconfig.json";
import { action, runInAction } from "mobx";
import {setThemeData} from '../Models/GraphList/GraphList';

/**
 * Updates the  {@link Terria} when the window's 'hashchange' event is raised.  This allows new init files and
 * "start=" URLs to be loaded just by changing the hash portion of the URL in the browser's address bar.
 *
 * @param  {Terria} terria The Terria instance to update.
 * @param {Window} window The browser's window DOM object.
 */
export default function (terria: Terria,viewState:any, window: Window) {
  window.addEventListener(
    "hashchange",
    async function () {
      try {
        let hashString:any = window.location.hash;
        if(hashString.indexOf("#pageNum=") > -1){
          let hashArray:any = hashString.split("#")[1].split("&");
          let searchParams:any = {};
          if(hashArray.length > 0){
            for(let i = 0; i < hashArray.length; i++){
              let tempArray:any =  hashArray[i].split("=");
              if(tempArray[0] && tempArray[1]){
                searchParams[tempArray[0]] = tempArray[1];
              }
            }
            if(searchParams["pageNum"]){
              runInAction(() => {
                viewState.setAdminPageNum(Number(searchParams["pageNum"]));
              })
            }
        }
        }
      } catch (e) {
        terria.raiseErrorToUser(e);
      }
    },
    false
  );
}
