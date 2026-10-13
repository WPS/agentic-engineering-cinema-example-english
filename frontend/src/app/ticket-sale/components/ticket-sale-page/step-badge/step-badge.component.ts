import {Component, input} from '@angular/core';

@Component({
  selector: 'app-step-badge',
  imports: [],
  templateUrl: './step-badge.component.html',
})
export class StepBadgeComponent {

  readonly text = input<string>("");

  readonly checked = input<boolean>(false);

}
