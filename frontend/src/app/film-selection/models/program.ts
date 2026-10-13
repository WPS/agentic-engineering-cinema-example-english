import {LocalizedText} from '../../common/i18n/localized-text';

export interface Film {
  title: string
  runtimeMinutes: number
  posterUrl: string
  ageRating: number
  description: LocalizedText
  genre: LocalizedText
  leadActors: string
  director: string
  language: LocalizedText
  screenings: Screening[]
}

export interface Screening {
  id: string
  start: string
  hall: LocalizedText
}
