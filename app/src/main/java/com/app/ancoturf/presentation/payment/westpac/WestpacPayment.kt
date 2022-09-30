package com.app.ancoturf.presentation.payment.westpac

class WestpacPayment {
    companion object {
        //sandbox
        val publicKey: String =
            "T13151_PUB_2f7sjnqrzpb9k8kaxg486569zmsvdbepgreqnyeydjjtb2acn3z9efa4atdv"
        val secretKey: String =
            "T13151_SEC_ag2tc2mz3qzr7crxfhpsprs7xqz4kwtwmjzfccv24ae33wkr6cjkqj8sefns"
        val westPackBaseUrl: String = "https://api.payway.com.au/rest/v1/"
        val MERCHANT_ID: String = "TEST"
        val HTTP_FILE_NAME: String = "SandBox.html"

        //live

      /*  val publicKey: String =
            "Q26891_PUB_qcep85d3jace9e7ptgj7jx23zmefzzmdhqp9nxwcindmzgbw6ta9k8rgu8kn"
        val secretKey: String =
            "Q26891_SEC_rj53ke293vv9jr4i5mnqmfjr3p3cqtcnpiwqy9mkd6h2wjdyf6avy2krexde"
        val MERCHANT_ID: String = "26209890"
        val HTTP_FILE_NAME: String = "Live.html"
        val westPackBaseUrl: String = "https://api.payway.com.au/rest/v1/"*/
    }
}
