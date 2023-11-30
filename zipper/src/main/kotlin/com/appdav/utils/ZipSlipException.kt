package com.appdav.utils

class ZipSlipException(entryName: String) :
    IllegalArgumentException("Zip archive contains zip-slip exploit which can cause damage to your system.\nEntry name: $entryName")
