// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  mode: 'online' as 'online' | 'offline',
  demo: false,
  serverUri: 'http://localhost:4300/proxy',
  loginClientId: '0C8AZZ16LZB4',
  noneLoginClientId: '0C8B00098WLD',
  clientSecret: '97b29ceb-c445-4178-bb95-84755f14cba6',
  codeCooldown: 20,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/dist/zone-error';  // Included with Angular CLI.
