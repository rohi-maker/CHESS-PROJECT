const DEV_SERVER = 'http://170.64.191.28:8000'

export default function serverUrl(relPath) {
  return (process.env.NODE_ENV === 'production')
    ? relPath
    : DEV_SERVER + relPath
}

export const RECAPTCHA_SITE_KEY = '6Lcmx04UAAAAAAkpSrzr2lDEqhjIpYKL30oODrCh'
