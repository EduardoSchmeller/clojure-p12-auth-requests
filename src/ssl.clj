(ns ssl
    (:require
      [clojure.java.io :as io]
      [less.awful.ssl :as ssl])
    (:import
      [java.io InputStream]
      [java.security KeyStore]
      [javax.net.ssl KeyManager SSLContext TrustManager]))
  
  (defn p12-cert->ssl-context
    "Transform an InputStream providing a P12 certificate into an SSL Context"
    [^InputStream p12]
        (let [key-store (KeyStore/getInstance "PKCS12")
              context (SSLContext/getInstance "TLS")
              password (char-array "password")]
          (.load key-store p12 password)
          (.init context
                 (into-array KeyManager [(ssl/key-manager key-store password)])
                 (into-array TrustManager [(ssl/trust-manager (.load (KeyStore/getInstance "PKCS12") nil nil))])
                 nil)
          context))
      
  (defn generate-ssl-context
   "Builds an SSL Context from a given certificate file path"
    [path]
        (let [certificate-path (str path)
              certificate-input-stream (io/input-stream certificate-path)]
          (-> (p12-cert->ssl-context certificate-input-stream))))
      
  (defn create-ssl-engine
    "Builds an SSL Engine from a given SSL Context"
    [ssl-context]
        (-> ssl-context (.createSSLEngine)))
  
  "example:"
  (create-ssl-engine (generate-ssl-context "/Users/your.name/Documents/certificate.p12"))