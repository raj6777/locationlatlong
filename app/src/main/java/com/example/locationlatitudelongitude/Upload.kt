package com.example.locationlatitudelongitude

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.IgnoreExtraProperties


/**
 * Created by Belal on 2/23/2017.
 */
@IgnoreExtraProperties
class Upload {
    var url: String? = null

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(url: Task<Uri>) {
        this.url = url.toString()
    }
}