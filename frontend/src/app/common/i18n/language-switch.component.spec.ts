import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LanguageSwitchComponent} from './language-switch.component';
import {LanguageService} from './language.service';

describe('LanguageSwitchComponent', () => {
  let fixture: ComponentFixture<LanguageSwitchComponent>;
  let languageService: LanguageService;

  function button(language: string): HTMLButtonElement {
    return fixture.nativeElement.querySelector(`button[lang="${language}"]`) as HTMLButtonElement;
  }

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [LanguageSwitchComponent],
    }).compileComponents();

    languageService = TestBed.inject(LanguageService);
    fixture = TestBed.createComponent(LanguageSwitchComponent);
    await fixture.whenStable();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('marks German as pressed by default', () => {
    expect(button('de').getAttribute('aria-pressed')).toBe('true');
    expect(button('en').getAttribute('aria-pressed')).toBe('false');
  });

  it('switches to English when EN is clicked', async () => {
    button('en').click();
    await fixture.whenStable();

    expect(languageService.language()).toBe('en');
    expect(button('en').getAttribute('aria-pressed')).toBe('true');
    expect(button('de').getAttribute('aria-pressed')).toBe('false');
    expect(button('en').classList).toContain('btn-active');
  });

  it('labels the group in the current language', async () => {
    const group = fixture.nativeElement.querySelector('[role="group"]') as HTMLElement;
    expect(group.getAttribute('aria-label')).toBe('Sprache');

    button('en').click();
    await fixture.whenStable();
    expect(group.getAttribute('aria-label')).toBe('Language');
  });
});
