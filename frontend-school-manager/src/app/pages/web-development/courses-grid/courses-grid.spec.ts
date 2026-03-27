import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesGrid } from './courses-grid';

describe('CoursesGrid', () => {
  let component: CoursesGrid;
  let fixture: ComponentFixture<CoursesGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursesGrid]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoursesGrid);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
