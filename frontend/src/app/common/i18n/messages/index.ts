import {Language} from '../language';
import {commonMessages} from './common.messages';
import {filmSelectionMessages} from './film-selection.messages';
import {paymentMessages} from './payment.messages';
import {ticketSaleMessages} from './ticket-sale.messages';

/**
 * All UI messages. Each feature area keeps its own file (keys prefixed with the area) and is
 * merged here.
 */
const ALL = [
  commonMessages,
  filmSelectionMessages,
  ticketSaleMessages,
  paymentMessages,
] as const;

type Merge<T extends readonly {de: Record<string, string>}[]> = T[number]['de'];
type UnionToIntersection<U> = (U extends unknown ? (u: U) => void : never) extends (i: infer I) => void ? I : never;

export type MessageKey = keyof UnionToIntersection<Merge<typeof ALL>>;

export const MESSAGES: Record<Language, Record<MessageKey, string>> = {
  de: Object.assign({}, ...ALL.map(m => m.de)),
  en: Object.assign({}, ...ALL.map(m => m.en)),
};
