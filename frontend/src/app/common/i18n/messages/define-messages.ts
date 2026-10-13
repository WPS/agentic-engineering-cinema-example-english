/**
 * Declares the messages of one feature area in both languages. The German set defines the keys;
 * the English set must provide exactly the same keys, so a missing or stray translation is a
 * compile error.
 *
 * Placeholders are written as `{name}` and filled by `translate(key, {name: value})`.
 */
export function defineMessages<K extends string>(
  de: Record<K, string>,
  en: Record<NoInfer<K>, string>,
): {de: Record<K, string>; en: Record<K, string>} {
  return {de, en};
}
