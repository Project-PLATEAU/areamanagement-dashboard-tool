"use strict";

import Terria from "../Models/Terria";

/**
 * Updates the  {@link Terria} when the window's 'hashchange' event is raised.  This allows new init files and
 * "start=" URLs to be loaded just by changing the hash portion of the URL in the browser's address bar.
 *
 * @param {Window} window The browser's window DOM object.
 */
export default function(window: Window) {
    if (!window.location.hash.includes("hidden")) {
      if(!window.location.hash || window.location.hash === ""){
        window.location.hash += "hidden=1";
      }else{
        window.location.hash += "&hidden=1"
      }
    }
}
