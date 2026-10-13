import {Component, input} from '@angular/core';
import {LanguageSwitchComponent} from '../../i18n/language-switch.component';
import {TranslatePipe} from '../../i18n/translate.pipe';

@Component({
  selector: 'app-navbar',
  imports: [LanguageSwitchComponent, TranslatePipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
   readonly title = input<string>("n/a");

}
