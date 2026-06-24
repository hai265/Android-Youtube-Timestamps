//
//  TimestamperApp.swift
//  Timestamper
//
//  Created by Hai Nguyen on 6/23/26.
//

import SwiftUI
import sharedUiKit

@main
struct TimestamperApp: App {

    init() {
        KoinInitIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
